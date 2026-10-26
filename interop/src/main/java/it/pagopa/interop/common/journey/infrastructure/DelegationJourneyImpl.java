package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.DelegationJourney;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DelegationJourneyImpl implements DelegationJourney<DelegationJourneyImpl> {

    private final DelegationUseCase delegationUseCase;
    private final EntityStore entityStore;

    @Override
    public DelegationJourneyImpl createProducerDelegation(@NonNull ProducerDelegationCreationCommand command) {
        delegationUseCase.createProducerDelegation(command);
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

}
