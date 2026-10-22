package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.DelegationJourney;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
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
    private final EntityStore entityStore;

    @Override
    public DelegationJourneyImpl createWaitingForApprovalProducerDelegation(
            @NonNull Tenant delegator,
            @NonNull Tenant delegate,
            @NonNull EServiceRef eServiceRef
    ) {
        prepareProducerDelegation(delegator, delegate, eServiceRef);
        return this;
    }

    @Override
    public DelegationJourneyImpl createActiveProducerDelegation(
            @NonNull Tenant delegator,
            @NonNull Tenant delegate,
            @NonNull EServiceRef eServiceRef
    ) {
        Delegation delegation = prepareProducerDelegation(delegator, delegate, eServiceRef);

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
    public DelegationJourneyImpl createProducerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        delegationUseCase.createProducerDelegation(delegateRef, eServiceRef);
        return this;
    }

    @Override
    public DelegationJourneyImpl createProducerDelegation(TenantRef delegateRef) {
        delegationUseCase.createProducerDelegation(delegateRef, entityStore.getLastOrThrow(EService.class).getRef());
        return this;
    }

    @Override
    public DelegationJourneyImpl approveConsumerDelegation(DelegationRef delegationRef) {
        delegationUseCase.approveConsumerDelegation(delegationRef);
        return this;
    }

    private Delegation prepareProducerDelegation(Tenant delegator, Tenant delegate, EServiceRef eServiceRef) {
        TenantRef delegateRef = TenantRef.of(delegate.getOrganizationId());
        userJourney.withProducer(delegate, UserRole.ADMIN);
        tenantUseCase.enableProducerDelegationAvailability(delegateRef);

        userJourney.withProducer(delegator, UserRole.ADMIN);
        return delegationUseCase.createProducerDelegation(delegateRef, eServiceRef);
    }
}
