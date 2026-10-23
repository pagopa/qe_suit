package it.pagopa.interop.common.delegation.application;

import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DelegationUseCase {

    private final DelegationGateway delegationGateway;

    public void approveProducerDelegation(DelegationRef delegationRef) {
        delegationGateway.approveProducerDelegation(delegationRef);
    }

    public Delegation createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        return delegationGateway.createConsumerDelegation(delegateRef, eServiceRef);
    }

    public Delegation createProducerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        return delegationGateway.createProducerDelegation(delegateRef, eServiceRef);
    }

    public void approveConsumerDelegation(DelegationRef delegationRef) {
        delegationGateway.approveConsumerDelegation(delegationRef);
    }
}
