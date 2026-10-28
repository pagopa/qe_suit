package it.pagopa.interop.common.delegation.application;

import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DelegationUseCase {

    private final DelegationGateway delegationGateway;

    public Delegation createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        return delegationGateway.createConsumerDelegation(delegateRef, eServiceRef);
    }

    public Delegation createProducerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        return delegationGateway.createProducerDelegation(delegateRef, eServiceRef);
    }

    public ProducerDelegation createProducerDelegation(
            TenantRef delegatorRef,
            TenantRef delegateRef,
            EService eService
    ) {
        if (!delegatorRef.id().toString().equals(eService.getProducerId())) {
            throw new IllegalArgumentException(
                    "L'e-service " + eService.getId() + " appartiene al delegante "
                            + eService.getProducerId() + ", non a " + delegatorRef.id()
            );
        }
        return delegationGateway.createProducerDelegation(delegatorRef, delegateRef, eService.getRef());
    }

    public void approveConsumerDelegation(DelegationRef delegationRef) {
        delegationGateway.approveConsumerDelegation(delegationRef);
    }

    public void approveProducerDelegation(DelegationRef delegationRef) {
        delegationGateway.approveProducerDelegation(delegationRef);
    }

    public void rejectProducerDelegation(DelegationRef delegationRef) {
        delegationGateway.rejectProducerDelegation(delegationRef);
    }

    public void revokeProducerDelegation(DelegationRef delegationRef) {
        delegationGateway.revokeProducerDelegation(delegationRef);
    }

    public ProducerDelegation getProducerDelegation(DelegationRef delegationRef) {
        return delegationGateway.getProducerDelegation(delegationRef);
    }

    public ProducerDelegation waitForProducerDelegationState(
            DelegationRef delegationRef,
            ProducerDelegationState expectedState
    ) {
        return delegationGateway.waitForProducerDelegationState(delegationRef, expectedState);
    }
}
