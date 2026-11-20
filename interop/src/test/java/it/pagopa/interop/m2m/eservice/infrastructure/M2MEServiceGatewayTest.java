package it.pagopa.interop.m2m.eservice.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.EservicesApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
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
