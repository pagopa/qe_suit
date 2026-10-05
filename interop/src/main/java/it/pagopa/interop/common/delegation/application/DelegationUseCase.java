package it.pagopa.interop.common.delegation.application;

import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DelegationUseCase {

    private final DelegationGateway delegationGateway;

    public Delegation createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        return delegationGateway.createConsumerDelegation(delegateRef, eServiceRef);
    }

    public void approveConsumerDelegation(UUID delegationId) {
        delegationGateway.approveConsumerDelegation(delegationId);
    }
}
