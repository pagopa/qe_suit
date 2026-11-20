package it.pagopa.interop.m2m.eservice.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceDraftUpdateSeed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class M2MEServiceGatewayTest {

    private final InMemoryEntityStore store = new InMemoryEntityStore();
    private final ApiClient apiClient = mock(ApiClient.class);
    private final EservicesApi api = mock(EservicesApi.class);
    private final LastApiResponseStore lastResponseStore = mock(LastApiResponseStore.class);
    private final UUID eServiceId = UUID.randomUUID();
    private final UUID producerId = UUID.randomUUID();
    private final EServiceRef eServiceRef = EServiceRef.of(eServiceId);
    private M2MEServiceGateway gateway;

    @BeforeEach
    void setUp() {
        when(apiClient.eservices()).thenReturn(api);
        gateway = new M2MEServiceGateway(
                new M2MEServiceRestClient(new TestChainFactory(lastResponseStore, store), apiClient),
                org.mapstruct.factory.Mappers.getMapper(M2MEServiceMapper.class),
                store,
                lastResponseStore
        );
    }

    @Test
    void updatesMetadataThroughDraftUpdateEndpointAndRefreshesContext() {
        EService initial = EService.builder()
                .id(eServiceId)
                .producerId(producerId.toString())
                .name("old name")
                .description("old description")
                .descriptors(List.of())
                .build();
        store.upsert("service", initial);

        EservicesApi.UpdateDraftEServiceOper update = mock(EservicesApi.UpdateDraftEServiceOper.class);
        EservicesApi.GetEServiceOper read = mock(EservicesApi.GetEServiceOper.class);

        when(api.updateDraftEService()).thenReturn(update);
        when(update.eserviceIdPath(eServiceId)).thenReturn(update);
        when(update.body(any(EServiceDraftUpdateSeed.class))).thenReturn(update);
        when(update.execute(any())).thenAnswer(invocation -> response(invocation, remoteEService(
                "new service name", "new service description"
        )));
        when(api.getEService()).thenReturn(read);
        when(read.eserviceIdPath(eServiceId)).thenReturn(read);
        when(read.execute(any())).thenAnswer(invocation -> response(invocation, remoteEService(
                "new service name", "new service description"
        )));

        var command = new it.pagopa.interop.m2m.eservice.application.M2MEServiceUpdateCommand()
                .alias("service")
                .name("new service name")
                .description("new service description");
        EServiceDraftUpdateSeed expectedPayload = new EServiceDraftUpdateSeed()
                .name("new service name")
                .description("new service description");
        assertThat(command.getM2mUpdatePayload()).isEqualTo(expectedPayload);

        gateway.updateEServiceMetadata(eServiceRef, command);

        verify(update).body(expectedPayload);
        verify(api).updateDraftEService();
        assertThat(store.getByAliasOrThrow("service", EService.class))
                .satisfies(updated -> {
                    assertThat(updated.getName()).isEqualTo("new service name");
                    assertThat(updated.getDescription()).isEqualTo("new service description");
                    assertThat(updated.getDescriptors()).isEqualTo(initial.getDescriptors());
                });
    }

    @Test
    void updatesDraftMetadataWithOnePatchAndPreservesUnspecifiedFields() {
        EService initial = EService.builder()
                .id(eServiceId)
                .producerId(producerId.toString())
                .name("old name")
                .description("old description")
                .descriptors(List.of())
                .build();
        store.upsert("service", initial);

        EservicesApi.UpdateDraftEServiceOper update = mock(EservicesApi.UpdateDraftEServiceOper.class);
        EservicesApi.GetEServiceOper read = mock(EservicesApi.GetEServiceOper.class);
        when(api.updateDraftEService()).thenReturn(update);
        when(update.eserviceIdPath(eServiceId)).thenReturn(update);
        when(update.body(any(EServiceDraftUpdateSeed.class))).thenReturn(update);
        when(update.execute(any())).thenAnswer(invocation -> response(invocation, remoteEService(
                "new service name", "old description"
        )));
        when(api.getEService()).thenReturn(read);
        when(read.eserviceIdPath(eServiceId)).thenReturn(read);
        when(read.execute(any())).thenAnswer(invocation -> response(invocation, remoteEService(
                "new service name", "old description"
        )));

        var command = new it.pagopa.interop.m2m.eservice.application.M2MEServiceUpdateCommand()
                .alias("service")
                .name("new service name");
        gateway.updateEServiceMetadata(eServiceRef, command);

        verify(update).body(new EServiceDraftUpdateSeed().name("new service name"));
        assertThat(store.getByAliasOrThrow("service", EService.class))
                .satisfies(updated -> {
                    assertThat(updated.getName()).isEqualTo("new service name");
                    assertThat(updated.getDescription()).isEqualTo("old description");
                    assertThat(updated.getDescriptors()).isEqualTo(initial.getDescriptors());
                });
    }

    @Test
    void requestFactoryCreatesAChannelSpecificUpdateCommand() {
        M2MEServiceRequestFactory requestFactory = new M2MEServiceRequestFactory();

        assertThat(requestFactory.supports(Channel.M2M_V3)).isTrue();
        assertThat(requestFactory.supports(Channel.BFF)).isFalse();
        assertThat(requestFactory.defaultUpdateEServiceCommand())
                .isInstanceOf(it.pagopa.interop.m2m.eservice.application.M2MEServiceUpdateCommand.class);
    }

    private String remoteEService(String name, String description) {
        return """
                {
                  "id": "%s",
                  "producerId": "%s",
                  "name": "%s",
                  "description": "%s",
                  "technology": "REST",
                  "mode": "DELIVER"
                }
                """.formatted(eServiceId, producerId, name, description);
    }

    private static Object response(org.mockito.invocation.InvocationOnMock invocation, String body) {
        Function<Response, Response> handler = invocation.getArgument(0);
        return handler.apply(new ResponseBuilder()
                .setStatusCode(200)
                .setContentType(ContentType.JSON)
                .setBody(body)
                .build());
    }
}
