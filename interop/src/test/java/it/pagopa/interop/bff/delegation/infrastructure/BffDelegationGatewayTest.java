package it.pagopa.interop.bff.delegation.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.CucumberEntityStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.delegation.domain.ProducerDelegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.DelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerDelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Delegation;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BffDelegationGatewayTest {

    @Test
    void waitsForRequestedStateAndUpdatesEntityStoreWithFreshSnapshot() {
        UUID delegationId = UUID.randomUUID();
        var store = new CucumberEntityStore();
        ApiClient apiClient = mock(ApiClient.class);
        DelegationsApi delegationsApi = mock(DelegationsApi.class);
        DelegationsApi.GetDelegationOper read = mock(DelegationsApi.GetDelegationOper.class);
        when(apiClient.delegations()).thenReturn(delegationsApi);
        when(delegationsApi.getDelegation()).thenReturn(read);
        when(read.delegationIdPath(delegationId)).thenReturn(read);
        when(read.execute(any())).thenAnswer(invocation -> {
            Function<io.restassured.response.Response, io.restassured.response.Response> handler =
                    invocation.getArgument(0);
            return handler.apply(new ResponseBuilder()
                    .setStatusCode(200)
                    .setContentType(ContentType.JSON)
                    .setBody(delegationBody(delegationId))
                    .build());
        });
        var restClient = new BffDelegationRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), store),
                apiClient
        );
        var mapper = org.mapstruct.factory.Mappers.getMapper(BffDelegationMapper.class);
        var gateway = new BffDelegationGateway(restClient, mapper, new BffDelegationRequestFactory());

        ProducerDelegation result = gateway.waitForProducerDelegationState(
                it.pagopa.interop.common.kernel.domain.DelegationRef.of(delegationId),
                ProducerDelegationState.ACTIVE
        );

        assertThat(result.getState()).isEqualTo(ProducerDelegationState.ACTIVE);
        assertThat(store.getById(delegationId, ProducerDelegation.class)).contains(result);
        assertThat(gateway.supports(Channel.BFF)).isTrue();
        assertThat(gateway.supports(Channel.WEB_BROWSER)).isFalse();
        verify(read).delegationIdPath(delegationId);
    }

    @Test
    void doesNotRetryDefinitiveProducerDelegationWriteFailures() {
        ApiClient apiClient = mock(ApiClient.class);
        ProducerDelegationsApi producerApi = mock(ProducerDelegationsApi.class);
        ProducerDelegationsApi.CreateProducerDelegationOper create =
                mock(ProducerDelegationsApi.CreateProducerDelegationOper.class);
        when(apiClient.producerDelegations()).thenReturn(producerApi);
        when(producerApi.createProducerDelegation()).thenReturn(create);
        when(create.body(any(DelegationSeed.class))).thenReturn(create);
        when(create.execute(any())).thenAnswer(invocation -> {
            Function<io.restassured.response.Response, io.restassured.response.Response> handler =
                    invocation.getArgument(0);
            return handler.apply(new ResponseBuilder()
                    .setStatusCode(400)
                    .setContentType(ContentType.JSON)
                    .setBody("{\"title\":\"bad request\"}")
                    .build());
        });
        var store = new CucumberEntityStore();
        var restClient = new BffDelegationRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), store),
                apiClient
        );
        var gateway = new BffDelegationGateway(
                restClient,
                org.mapstruct.factory.Mappers.getMapper(BffDelegationMapper.class),
                new BffDelegationRequestFactory()
        );

        assertThatThrownBy(() -> gateway.createProducerDelegation(
                TenantRef.of(Tenant.COMUNE_DI_POZZALLO.getOrganizationId()),
                EServiceRef.of(UUID.randomUUID())
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("HTTP 400");

        verify(create, times(1)).execute(any());
    }

    private static String delegationBody(UUID id) {
        return """
                {
                  "id": "%s",
                  "state": "ACTIVE",
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
                id,
                it.pagopa.interop.common.kernel.domain.Tenant.COMUNE_DI_MILANO.getOrganizationId(),
                it.pagopa.interop.common.kernel.domain.Tenant.COMUNE_DI_POZZALLO.getOrganizationId(),
                UUID.randomUUID(),
                it.pagopa.interop.common.kernel.domain.Tenant.COMUNE_DI_MILANO.getOrganizationId()
        );
    }
}
