package it.pagopa.interop.bff.agreement.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BffAgreementLifecycleTransportTest {
    @Test
    void suspendsUsingGeneratedOperationAndPassesOptionalDelegationReference() {
        ApiClient apiClient = mock(ApiClient.class);
        AgreementsApi api = mock(AgreementsApi.class);
        AgreementsApi.SuspendAgreementOper operation = mock(AgreementsApi.SuspendAgreementOper.class);
        UUID agreementId = UUID.randomUUID();
        UUID delegationId = UUID.randomUUID();
        when(apiClient.agreements()).thenReturn(api);
        when(api.suspendAgreement()).thenReturn(operation);
        when(operation.agreementIdPath(agreementId)).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> response(invocation.getArgument(0)));
        var client = restClient(apiClient);

        client.suspend(agreementId, delegationId).withoutPolling().assertStatusCode(200);

        var body = org.mockito.ArgumentCaptor.forClass(DelegationRef.class);
        verify(operation).body(body.capture());
        assertThat(body.getValue().getDelegationId()).isEqualTo(delegationId);
        verify(operation).agreementIdPath(agreementId);
        assertThat(AgreementsApi.SuspendAgreementOper.REQ_METHOD.name()).isEqualTo("POST");
        assertThat(AgreementsApi.SuspendAgreementOper.REQ_URI).isEqualTo("/agreements/{agreementId}/suspend");
        verify(operation, times(1)).execute(any());
    }

    @Test
    void archivesUsingTheGeneratedTerminalTransitionWithoutInventingAPayload() {
        ApiClient apiClient = mock(ApiClient.class);
        AgreementsApi api = mock(AgreementsApi.class);
        AgreementsApi.ArchiveAgreementOper operation = mock(AgreementsApi.ArchiveAgreementOper.class);
        UUID agreementId = UUID.randomUUID();
        when(apiClient.agreements()).thenReturn(api);
        when(api.archiveAgreement()).thenReturn(operation);
        when(operation.agreementIdPath(agreementId)).thenReturn(operation);
        when(operation.reqSpec(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation ->
                response(invocation.getArgument(0), 204, ""));
        var client = restClient(apiClient);

        assertThat(client.archive(agreementId).withoutPolling().assertStatusCode(204).get()).isNull();

        verify(operation).agreementIdPath(agreementId);
        verify(operation).reqSpec(any());
        assertThat(AgreementsApi.ArchiveAgreementOper.REQ_METHOD.name()).isEqualTo("POST");
        assertThat(AgreementsApi.ArchiveAgreementOper.REQ_URI).isEqualTo("/agreements/{agreementId}/archive");
        verify(operation, times(1)).execute(any());
        verifyNoMoreInteractions(operation);
    }

    @Test
    void archivesEmptyResponseThenReadsUntilArchivedAndStoresFreshSnapshot() {
        ApiClient apiClient = mock(ApiClient.class);
        AgreementsApi api = mock(AgreementsApi.class);
        var archive = mock(AgreementsApi.ArchiveAgreementOper.class);
        var read = mock(AgreementsApi.GetAgreementByIdOper.class);
        UUID id = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        UUID producerId = UUID.randomUUID();
        UUID consumerId = UUID.randomUUID();
        UUID eServiceId = UUID.randomUUID();
        var entities = new InMemoryEntityStore();
        AtomicInteger reads = new AtomicInteger();
        when(apiClient.agreements()).thenReturn(api);
        when(api.archiveAgreement()).thenReturn(archive);
        when(archive.agreementIdPath(id)).thenReturn(archive);
        when(archive.reqSpec(any())).thenReturn(archive);
        when(archive.execute(any())).thenAnswer(invocation ->
                response(invocation.getArgument(0), 204, ""));
        when(api.getAgreementById()).thenReturn(read);
        when(read.agreementIdPath(id)).thenReturn(read);
        when(read.execute(any())).thenAnswer(invocation -> response(invocation.getArgument(0),
                agreementJson(id, descriptorId, producerId, consumerId, eServiceId,
                        reads.getAndIncrement() == 0 ? "ACTIVE" : "ARCHIVED")));
        var gateway = new BffAgreementGateway(
                new BffAgreementRestClient(
                        new TestChainFactory(mock(LastApiResponseStore.class), entities), apiClient),
                Mappers.getMapper(BffAgreementMapper.class), new BffAgreementRequestFactory(),
                mock(BffAgreementPrerequisiteGateway.class));

        Agreement result = gateway.archiveAgreement(
                Agreement.builder().id(id).state(AgreementState.ACTIVE).build());

        assertThat(result.getState()).isEqualTo(AgreementState.ARCHIVED);
        assertThat(entities.getById(id, Agreement.class)).contains(result);
        verify(archive, times(1)).execute(any());
        verify(read, times(2)).execute(any());
    }

    @Test
    void failsArchiveWithoutRetryingOrReadingWhenMutationIsRejected() {
        ApiClient apiClient = mock(ApiClient.class);
        AgreementsApi api = mock(AgreementsApi.class);
        var archive = mock(AgreementsApi.ArchiveAgreementOper.class);
        UUID id = UUID.randomUUID();
        when(apiClient.agreements()).thenReturn(api);
        when(api.archiveAgreement()).thenReturn(archive);
        when(archive.agreementIdPath(id)).thenReturn(archive);
        when(archive.reqSpec(any())).thenReturn(archive);
        when(archive.execute(any())).thenAnswer(invocation ->
                response(invocation.getArgument(0), 403, ""));
        var gateway = new BffAgreementGateway(restClient(apiClient),
                Mappers.getMapper(BffAgreementMapper.class), new BffAgreementRequestFactory(),
                mock(BffAgreementPrerequisiteGateway.class));

        assertThatThrownBy(() -> gateway.archiveAgreement(
                Agreement.builder().id(id).state(AgreementState.ACTIVE).build()))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("204").hasMessageContaining("403");

        verify(archive, times(1)).execute(any());
        verify(api, never()).getAgreementById();
    }

    @Test
    void pollsAgreementStateAndStoresTheLatestMappedSnapshot() {
        ApiClient apiClient = mock(ApiClient.class);
        AgreementsApi api = mock(AgreementsApi.class);
        AgreementsApi.GetAgreementByIdOper operation = mock(AgreementsApi.GetAgreementByIdOper.class);
        UUID agreementId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        UUID producerId = UUID.randomUUID();
        UUID consumerId = UUID.randomUUID();
        UUID eServiceId = UUID.randomUUID();
        AtomicInteger attempts = new AtomicInteger();
        var entities = new InMemoryEntityStore();
        when(apiClient.agreements()).thenReturn(api);
        when(api.getAgreementById()).thenReturn(operation);
        when(operation.agreementIdPath(agreementId)).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            int attempt = attempts.getAndIncrement();
            String state = attempt == 0 ? "PENDING" : "ACTIVE";
            String body = agreementJson(
                    agreementId, descriptorId, producerId, consumerId, eServiceId, state
            );
            return response(invocation.getArgument(0), body);
        });
        var client = new BffAgreementRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), entities),
                apiClient
        );
        var gateway = new BffAgreementGateway(
                client,
                Mappers.getMapper(BffAgreementMapper.class),
                new BffAgreementRequestFactory(),
                mock(BffAgreementPrerequisiteGateway.class)
        );
        Agreement submitted = Agreement.builder().id(agreementId).state(AgreementState.PENDING).build();

        Agreement active = gateway.awaitState(submitted, Set.of(AgreementState.ACTIVE));

        assertThat(active.getState()).isEqualTo(AgreementState.ACTIVE);
        assertThat(entities.getById(agreementId, Agreement.class)).contains(active);
        assertThat(attempts.get()).isEqualTo(2);
        verify(operation, times(2)).agreementIdPath(agreementId);
    }

    @Test
    void failsImmediatelyOnRejectedAgreementInsteadOfWaitingForAnImpossibleState() {
        ApiClient apiClient = mock(ApiClient.class);
        AgreementsApi api = mock(AgreementsApi.class);
        AgreementsApi.GetAgreementByIdOper operation = mock(AgreementsApi.GetAgreementByIdOper.class);
        UUID agreementId = UUID.randomUUID();
        AtomicInteger attempts = new AtomicInteger();
        when(apiClient.agreements()).thenReturn(api);
        when(api.getAgreementById()).thenReturn(operation);
        when(operation.agreementIdPath(agreementId)).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            attempts.incrementAndGet();
            return response(invocation.getArgument(0), agreementJson(
                    agreementId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    UUID.randomUUID(), "REJECTED"
            ));
        });
        var client = new BffAgreementRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), new InMemoryEntityStore()),
                apiClient
        );
        var gateway = new BffAgreementGateway(
                client,
                Mappers.getMapper(BffAgreementMapper.class),
                new BffAgreementRequestFactory(),
                mock(BffAgreementPrerequisiteGateway.class)
        );

        assertThatThrownBy(() -> gateway.awaitState(
                Agreement.builder().id(agreementId).state(AgreementState.PENDING).build(),
                Set.of(AgreementState.ACTIVE)
        )).isInstanceOf(IllegalStateException.class).hasMessageContaining("REJECTED");
        assertThat(attempts.get()).isEqualTo(1);
    }

    private BffAgreementRestClient restClient(ApiClient apiClient) {
        return new BffAgreementRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)),
                apiClient
        );
    }

    private Response response(Function<Response, Response> handler) {
        return response(handler, "{}");
    }

    private Response response(Function<Response, Response> handler, String body) {
        return response(handler, 200, body);
    }

    private Response response(Function<Response, Response> handler, int status, String body) {
        return handler.apply(new ResponseBuilder()
                .setStatusCode(status)
                .setContentType(ContentType.JSON)
                .setBody(body)
                .build());
    }

    private String agreementJson(
            UUID agreementId,
            UUID descriptorId,
            UUID producerId,
            UUID consumerId,
            UUID eServiceId,
            String state
    ) {
        return "{\"id\":\"" + agreementId + "\",\"descriptorId\":\"" + descriptorId
                + "\",\"producer\":{\"id\":\"" + producerId + "\"},"
                + "\"consumer\":{\"id\":\"" + consumerId + "\"},"
                + "\"eservice\":{\"id\":\"" + eServiceId + "\"},"
                + "\"state\":\"" + state + "\",\"isContractPresent\":false,"
                + "\"createdAt\":\"2025-01-01T00:00:00Z\"}";
    }
}
