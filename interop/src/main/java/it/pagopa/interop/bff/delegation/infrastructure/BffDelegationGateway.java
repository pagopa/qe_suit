package it.pagopa.interop.bff.delegation.infrastructure;

import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.delegation.domain.ProducerDelegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.delegation.application.DelegationGateway;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class BffDelegationGateway implements DelegationGateway {

    private final BffDelegationRestClient restClient;
    private final BffDelegationMapper mapper;
    private final BffDelegationRequestFactory requestFactory;

    @Override
    public Delegation createConsumerDelegation(@NonNull TenantRef delegateRef, @NonNull EServiceRef eServiceRef) {
        return restClient.createConsumerDelegation(delegateRef.id(), eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toDelegation)
                .updateContext()
                .get();
    }

    @Override
    public Delegation createProducerDelegation(@NonNull TenantRef delegateRef, @NonNull EServiceRef eServiceRef) {
        var response = restClient.createProducerDelegation(delegateRef.id(), eServiceRef.id())
                .withoutPolling();
        requireSuccessfulResponse(response.getRaw(), "creazione della delega di erogazione");
        return response
                .map(mapper::toDelegation)
                .updateContext()
                .get();
    }

    @Override
    public ProducerDelegation createProducerDelegation(
            @NonNull TenantRef delegatorRef,
            @NonNull TenantRef delegateRef,
            @NonNull EServiceRef eServiceRef
    ) {
        var response = restClient.createProducerDelegation(delegateRef.id(), eServiceRef.id())
                .withoutPolling();
        requireSuccessfulResponse(response.getRaw(), "creazione della delega di erogazione");
        return response
                .map(mapper::toProducerDelegation)
                .updateContext()
                .get();
    }

    @Override
    public void approveConsumerDelegation(DelegationRef delegationRef) {
        restClient.approveConsumerDelegation(delegationRef.id()).withPolling(PollingStrategy.UNTIL_SUCCESS);
    }

    @Override
    public void approveProducerDelegation(DelegationRef delegationRef) {
        var response = restClient.approveProducerDelegation(delegationRef.id()).withoutPolling();
        requireSuccessfulResponse(response.getRaw(), "approvazione della delega di erogazione");
    }

    @Override
    public void rejectProducerDelegation(DelegationRef delegationRef) {
        var response = restClient.rejectProducerDelegation(
                delegationRef.id(), requestFactory.rejectionRequest()
        ).withoutPolling();
        requireSuccessfulResponse(response.getRaw(), "rifiuto della delega di erogazione");
    }

    @Override
    public void revokeProducerDelegation(DelegationRef delegationRef) {
        restClient.revokeProducerDelegation(delegationRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS);
    }

    @Override
    public ProducerDelegation getProducerDelegation(DelegationRef delegationRef) {
        return restClient
                .getProducerDelegation(delegationRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toProducerDelegation)
                .updateContext()
                .get();
    }

    @Override
    public ProducerDelegation waitForProducerDelegationState(
            DelegationRef delegationRef,
            ProducerDelegationState expectedState
    ) {
        return restClient.getProducerDelegation(delegationRef.id())
                .withPolling(
                        response -> hasExpectedState(response, expectedState),
                        Duration.ofSeconds(45),
                        Duration.ofSeconds(1)
                )
                .map(mapper::toProducerDelegation)
                .updateContext()
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private boolean hasExpectedState(RawResponse response, ProducerDelegationState expectedState) {
        if (!response.isSuccess()) {
            if (!(response instanceof ApiResponse apiResponse)) {
                throw new IllegalStateException(
                        "Risposta non valida durante la lettura dello stato della delega di erogazione"
                );
            }
            int status = apiResponse.getStatusCode();
            if (status == 404 || status == 409 || status == 429 || status >= 500) {
                return false;
            }
            throw new IllegalStateException(
                    "Errore durante la lettura dello stato della delega di erogazione (HTTP " + status + ")"
            );
        }
        it.pagopa.interop.generated.openapi.clients.bff.model.Delegation delegation =
                response.as(it.pagopa.interop.generated.openapi.clients.bff.model.Delegation.class);
        if (delegation.getState() == null) {
            throw new IllegalStateException("La delega di erogazione letta non contiene lo stato");
        }
        return ProducerDelegationState.valueOf(delegation.getState().name()) == expectedState;
    }

    private static void requireSuccessfulResponse(RawResponse response, String operation) {
        if (!response.isSuccess()) {
            String status = response instanceof ApiResponse apiResponse
                    ? Integer.toString(apiResponse.getStatusCode())
                    : "sconosciuto";
            throw new IllegalStateException(
                    "Errore durante " + operation + " (HTTP " + status + ")"
            );
        }
    }
}
