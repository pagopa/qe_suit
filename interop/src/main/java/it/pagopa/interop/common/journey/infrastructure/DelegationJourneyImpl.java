package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.journey.application.DelegationJourney;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DelegationJourneyImpl implements DelegationJourney<DelegationJourneyImpl> {

    private final DelegationUseCase delegationUseCase;

    @Override
    public DelegationJourneyImpl createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        delegationUseCase.createConsumerDelegation(delegateRef, eServiceRef);
        return this;
    }

    @Override
    public DelegationJourneyImpl approveConsumerDelegation(UUID delegationId) {
        delegationUseCase.approveConsumerDelegation(delegationId);
        return this;
    }
}
