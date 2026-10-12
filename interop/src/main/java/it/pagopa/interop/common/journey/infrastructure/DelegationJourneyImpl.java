package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.journey.application.DelegationJourney;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DelegationJourneyImpl implements DelegationJourney<DelegationJourneyImpl> {

    private final DelegationUseCase delegationUseCase;
    private final TenantUseCase tenantUseCase;
    private final UserJourneyImpl userJourney;

    @Override
    public DelegationJourneyImpl createActiveProducerDelegation(
            @NonNull Tenant delegator,
            @NonNull Tenant delegate,
            @NonNull EServiceRef eServiceRef
    ) {
        userJourney.withProducer(delegate, UserRole.ADMIN);
        tenantUseCase.enableProducerDelegationAvailability();

        userJourney.withProducer(delegator, UserRole.ADMIN);
        Delegation delegation = delegationUseCase.createProducerDelegation(
                TenantRef.of(delegate.getOrganizationId()), eServiceRef);

        userJourney.withProducer(delegate, UserRole.ADMIN);
        delegationUseCase.approveProducerDelegation(delegation.getRef());
        return this;
    }

    @Override
    public DelegationJourneyImpl createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        delegationUseCase.createConsumerDelegation(delegateRef, eServiceRef);
        return this;
    }

    @Override
    public DelegationJourneyImpl approveConsumerDelegation(DelegationRef delegationRef) {
        delegationUseCase.approveConsumerDelegation(delegationRef);
        return this;
    }
}
