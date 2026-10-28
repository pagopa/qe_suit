package it.pagopa.interop.bff.eservice.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingSeed;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BffEServiceArchivingTransportTest {
    @Test
    void archivesTheWholeServiceThroughItsOwnEndpointNotTheDescriptorEndpoint() {
        var apiClient = mock(ApiClient.class);
        var api = mock(EservicesApi.class);
        var operation = mock(EservicesApi.ScheduleArchiveEserviceOper.class);
        var ref = EServiceRef.of(UUID.randomUUID());
        when(apiClient.eservices()).thenReturn(api);
        when(api.scheduleArchiveEservice()).thenReturn(operation);
        when(operation.eServiceIdPath(ref.id())).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder().setStatusCode(204).build());
        });
        var entities = mock(EntityStore.class);
        var restClient = new BffEServiceRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), entities), apiClient);
        var gateway = new BffEServiceGateway(restClient, mock(BffEServiceDescriptorGateway.class),
                entities, mock(BffEServiceMapper.class));

        gateway.archiveEService(ref, GracePeriodDays.NUMBER_60);

        var payload = ArgumentCaptor.forClass(EServiceArchivingSeed.class);
        verify(operation).body(payload.capture());
        assertThat(payload.getValue().getGracePeriodDays().getValue()).isEqualTo(60);
        assertThat(payload.getValue().getArchivingReason()).isNotBlank();
        assertThat(EservicesApi.ScheduleArchiveEserviceOper.REQ_URI).isEqualTo("/eservices/{eServiceId}/scheduleArchive");
        verify(api, never()).scheduleArchiveEserviceDescriptor();
        verify(operation, times(1)).execute(any());
    }
}
