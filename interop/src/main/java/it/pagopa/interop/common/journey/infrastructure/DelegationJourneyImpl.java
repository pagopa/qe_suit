package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.delegation.application.command.ProducerDelegationSetupCommand;
import it.pagopa.interop.common.delegation.domain.ProducerDelegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.DelegationJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DelegationJourneyImpl implements DelegationJourney<DelegationJourneyImpl> {

    private final DelegationUseCase delegationUseCase;
    private final EntityStore entityStore;
    private final CurrentUserSession currentUserSession;
    private final CurrentChannel<Channel> currentChannel;

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

    @Override
    public DelegationJourneyImpl setUpProducerDelegation(ProducerDelegationSetupCommand command) {
        User originalUser = currentUserSession.getUser();
        Tenant originalTenant = currentUserSession.getTenant();
        Channel originalChannel = currentChannel.getCurrentChannel();

        try {
            currentChannel.setCurrentChannel(Channel.BFF);
            currentUserSession.set(User.getTenantAdmin(command.delegator()), command.delegator());
            ProducerDelegation delegation = delegationUseCase.createProducerDelegation(
                    TenantRef.of(command.delegator().getOrganizationId()),
                    TenantRef.of(command.delegate().getOrganizationId()),
                    command.eService()
            );
            delegationUseCase.waitForProducerDelegationState(
                    delegation.getRef(), ProducerDelegationState.WAITING_FOR_APPROVAL
            );
            transitionToRequestedState(command, delegation);
            return this;
        } finally {
            currentUserSession.set(originalUser, originalTenant);
            currentChannel.setCurrentChannel(originalChannel);
        }
    }

    private void transitionToRequestedState(
            ProducerDelegationSetupCommand command,
            ProducerDelegation delegation
    ) {
        switch (command.state()) {
            case WAITING_FOR_APPROVAL -> {
            }
            case ACTIVE -> approve(command.delegate(), delegation);
            case REJECTED -> reject(command.delegate(), delegation);
            case REVOKED -> {
                approve(command.delegate(), delegation);
                currentUserSession.set(User.getTenantAdmin(command.delegator()), command.delegator());
                delegationUseCase.revokeProducerDelegation(delegation.getRef());
                delegationUseCase.waitForProducerDelegationState(
                        delegation.getRef(), ProducerDelegationState.REVOKED
                );
            }
        }
    }

    private void approve(Tenant delegate, ProducerDelegation delegation) {
        currentUserSession.set(User.getTenantAdmin(delegate), delegate);
        delegationUseCase.approveProducerDelegation(delegation.getRef());
        delegationUseCase.waitForProducerDelegationState(
                delegation.getRef(), ProducerDelegationState.ACTIVE
        );
    }

    private void reject(Tenant delegate, ProducerDelegation delegation) {
        currentUserSession.set(User.getTenantAdmin(delegate), delegate);
        delegationUseCase.rejectProducerDelegation(delegation.getRef());
        delegationUseCase.waitForProducerDelegationState(
                delegation.getRef(), ProducerDelegationState.REJECTED
        );
    }
}
