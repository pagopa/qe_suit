package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BffEServiceDescriptorPublicationTransportTest {
    @Test
    void publishesThroughTheGeneratedDescriptorOperationUsedForDelegatedApproval() {
        ApiClient apiClient = mock(ApiClient.class);
        EservicesApi api = mock(EservicesApi.class);
        EservicesApi.PublishDescriptorOper operation = mock(EservicesApi.PublishDescriptorOper.class);
        UUID eServiceId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        when(apiClient.eservices()).thenReturn(api);
        when(api.publishDescriptor()).thenReturn(operation);
        when(operation.eServiceIdPath(eServiceId)).thenReturn(operation);
        when(operation.descriptorIdPath(descriptorId)).thenReturn(operation);
        when(operation.reqSpec(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> response(invocation.getArgument(0)));

        var client = new BffEServiceRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)),
                apiClient
        );
        client.publishDescriptor(eServiceId, descriptorId).withoutPolling().assertStatusCode(204).get();

        assertThat(EservicesApi.PublishDescriptorOper.REQ_METHOD.name()).isEqualTo("POST");
        assertThat(EservicesApi.PublishDescriptorOper.REQ_URI)
                .isEqualTo("/eservices/{eServiceId}/descriptors/{descriptorId}/publish");
        verify(operation).eServiceIdPath(eServiceId);
        verify(operation).descriptorIdPath(descriptorId);
        verify(operation, times(1)).execute(any());
    }

    private Response response(Function<Response, Response> handler) {
        return handler.apply(new ResponseBuilder()
                .setStatusCode(204)
                .setContentType(ContentType.JSON)
                .setBody("")
                .build());
    }
}
