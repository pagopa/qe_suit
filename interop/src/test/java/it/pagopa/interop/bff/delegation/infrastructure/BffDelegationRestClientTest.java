package it.pagopa.interop.bff.delegation.infrastructure;

import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.DelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerDelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Delegation;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BffDelegationRestClientTest {

    private final UUID delegationId = UUID.randomUUID();

    @Test
    void sendsProducerDelegationSeedAndReadsDelegationById() {
        ApiClient apiClient = mock(ApiClient.class);
        ProducerDelegationsApi producerApi = mock(ProducerDelegationsApi.class);
        DelegationsApi delegationsApi = mock(DelegationsApi.class);
        ProducerDelegationsApi.CreateProducerDelegationOper create =
                mock(ProducerDelegationsApi.CreateProducerDelegationOper.class);
        DelegationsApi.GetDelegationOper read = mock(DelegationsApi.GetDelegationOper.class);
        when(apiClient.producerDelegations()).thenReturn(producerApi);
        when(apiClient.delegations()).thenReturn(delegationsApi);
        when(producerApi.createProducerDelegation()).thenReturn(create);
        when(delegationsApi.getDelegation()).thenReturn(read);
        when(create.body(any())).thenReturn(create);
        when(create.execute(any())).thenAnswer(invocation -> apply(invocation.getArgument(0), delegationResponse(
                "WAITING_FOR_APPROVAL"
        )));
        when(read.delegationIdPath(delegationId)).thenReturn(read);
        when(read.execute(any())).thenAnswer(invocation -> apply(invocation.getArgument(0), delegationResponse(
                "ACTIVE"
        )));
        var client = client(apiClient);
        UUID delegateId = UUID.randomUUID();
        UUID eServiceId = UUID.randomUUID();

        client.createProducerDelegation(delegateId, eServiceId).withoutPolling().get();
        Delegation delegation = client.getProducerDelegation(delegationId).withoutPolling().get();

        var seed = org.mockito.ArgumentCaptor.forClass(DelegationSeed.class);
        verify(create).body(seed.capture());
        assertThat(seed.getValue().getDelegateId()).isEqualTo(delegateId);
        assertThat(seed.getValue().getEserviceId()).isEqualTo(eServiceId);
        verify(read).delegationIdPath(delegationId);
        assertThat(delegation.getState()).isEqualTo(
                it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState.ACTIVE
        );
        assertThat(DelegationsApi.GetDelegationOper.REQ_URI).isEqualTo("/delegations/{delegationId}");
    }

    @Test
    void sendsProducerApprovalRejectionAndRevocationToTheirGeneratedOperations() {
        ApiClient apiClient = mock(ApiClient.class);
        ProducerDelegationsApi producerApi = mock(ProducerDelegationsApi.class);
        var approve = mock(ProducerDelegationsApi.ApproveProducerDelegationOper.class);
        var reject = mock(ProducerDelegationsApi.RejectProducerDelegationOper.class);
        var revoke = mock(ProducerDelegationsApi.RevokeProducerDelegationOper.class);
        when(apiClient.producerDelegations()).thenReturn(producerApi);
        when(producerApi.approveProducerDelegation()).thenReturn(approve);
        when(producerApi.rejectProducerDelegation()).thenReturn(reject);
        when(producerApi.revokeProducerDelegation()).thenReturn(revoke);
        when(approve.delegationIdPath(delegationId)).thenReturn(approve);
        when(reject.delegationIdPath(delegationId)).thenReturn(reject);
        when(reject.body(any())).thenReturn(reject);
        when(revoke.delegationIdPath(delegationId)).thenReturn(revoke);
        when(approve.execute(any())).thenAnswer(invocation -> apply(invocation.getArgument(0), emptyResponse()));
        when(reject.execute(any())).thenAnswer(invocation -> apply(invocation.getArgument(0), emptyResponse()));
        when(revoke.execute(any())).thenAnswer(invocation -> apply(invocation.getArgument(0), emptyResponse()));
        var client = client(apiClient);

        client.approveProducerDelegation(delegationId).withoutPolling().get();
        client.rejectProducerDelegation(delegationId, new RejectDelegationPayload()
                .rejectionReason("Rifiutata nell'ambito del setup di test")).withoutPolling().get();
        client.revokeProducerDelegation(delegationId).withoutPolling().get();

        verify(approve).delegationIdPath(delegationId);
        verify(reject).delegationIdPath(delegationId);
        var payload = org.mockito.ArgumentCaptor.forClass(RejectDelegationPayload.class);
        verify(reject).body(payload.capture());
        assertThat(payload.getValue().getRejectionReason())
                .isEqualTo("Rifiutata nell'ambito del setup di test");
        verify(revoke).delegationIdPath(delegationId);
        assertThat(ProducerDelegationsApi.ApproveProducerDelegationOper.REQ_URI)
                .isEqualTo("/producers/delegations/{delegationId}/approve");
        assertThat(ProducerDelegationsApi.RejectProducerDelegationOper.REQ_URI)
                .isEqualTo("/producers/delegations/{delegationId}/reject");
        assertThat(ProducerDelegationsApi.RevokeProducerDelegationOper.REQ_METHOD.name()).isEqualTo("DELETE");
    }

    private static BffDelegationRestClient client(ApiClient apiClient) {
        return new BffDelegationRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)),
                apiClient
        );
    }

    private static Response apply(Function<Response, Response> handler, Response response) {
        return handler.apply(response);
    }

    private static Response delegationResponse(String state) {
        return new ResponseBuilder()
                .setStatusCode(200)
                .setContentType(ContentType.JSON)
                .setBody("""
                        {
                          "id": "%s",
                          "state": "%s",
                          "kind": "DELEGATED_PRODUCER",
                          "createdAt": "2026-10-22T12:00:00Z",
                          "isDocumentReady": false,
                          "delegator": {"id": "%s", "name": "Comune di Milano"},
                          "delegate": {"id": "%s", "name": "Comune di Pozzallo"},
                          "eservice": {
                            "id": "%s",
                            "name": "E-service test",
                            "producerId": "%s",
                            "producerName": "Comune di Milano",
                            "descriptors": []
                          }
                        }
                        """.formatted(
                        UUID.randomUUID(),
                        state,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                ))
                .build();
    }

    private static Response emptyResponse() {
        Response response = mock(Response.class);
        when(response.getStatusCode()).thenReturn(200);
        when(response.asString()).thenReturn("");
        return response;
    }
}
