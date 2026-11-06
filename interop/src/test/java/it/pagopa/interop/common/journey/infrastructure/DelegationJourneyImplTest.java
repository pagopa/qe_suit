package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.delegation.application.command.ProducerDelegationSetupCommand;
import it.pagopa.interop.common.delegation.domain.ProducerDelegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

class DelegationJourneyImplTest {

    private final DelegationUseCase useCase = mock(DelegationUseCase.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    private final CurrentChannel<Channel> channel = mock(CurrentChannel.class);
    private final it.pagopa.application.context.EntityStore entityStore =
            mock(it.pagopa.application.context.EntityStore.class);
    private final DelegationJourneyImpl journey =
            new DelegationJourneyImpl(useCase, entityStore, session, channel);
    private final Tenant delegator = Tenant.COMUNE_DI_MILANO;
    private final Tenant delegate = Tenant.COMUNE_DI_POZZALLO;
    private final EService eService = EService.builder()
            .id(UUID.randomUUID())
            .producerId(delegator.getOrganizationId().toString())
            .descriptors(new ArrayList<>())
            .build();
    private final ProducerDelegation created = ProducerDelegation.builder()
            .id(UUID.randomUUID())
            .state(ProducerDelegationState.WAITING_FOR_APPROVAL)
            .build();
    private User originalUser;
    private Tenant originalTenant;

    @BeforeEach
    void setUpSession() {
        originalUser = User.getTenantAdmin(Tenant.COMUNE_DI_COMUN_NUOVO);
        originalTenant = Tenant.COMUNE_DI_COMUN_NUOVO;
        when(session.getUser()).thenReturn(originalUser);
        when(session.getTenant()).thenReturn(originalTenant);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);
        when(useCase.createProducerDelegation(any(), any(), same(eService))).thenReturn(created);
    }

    @Test
    void leavesWaitingDelegationPendingAndRestoresOriginalContext() {
        journey.setUpProducerDelegation(command(ProducerDelegationState.WAITING_FOR_APPROVAL));

        InOrder order = inOrder(session, useCase);
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(useCase).createProducerDelegation(any(), any(), same(eService));
        order.verify(useCase).waitForProducerDelegationState(
                created.getRef(), ProducerDelegationState.WAITING_FOR_APPROVAL
        );
        verify(useCase, never()).approveProducerDelegation(any());
        verify(useCase, never()).rejectProducerDelegation(any());
        verify(useCase, never()).revokeProducerDelegation(any());
        verify(session).set(originalUser, originalTenant);
        verify(channel).setCurrentChannel(Channel.BFF);
        verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }

    @Test
    void approvesAsDelegateToCreateAnActiveDelegation() {
        journey.setUpProducerDelegation(command(ProducerDelegationState.ACTIVE));

        InOrder order = inOrder(session, useCase);
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(useCase).createProducerDelegation(any(), any(), same(eService));
        order.verify(useCase).waitForProducerDelegationState(
                created.getRef(), ProducerDelegationState.WAITING_FOR_APPROVAL
        );
        order.verify(session).set(User.getTenantAdmin(delegate), delegate);
        order.verify(useCase).approveProducerDelegation(created.getRef());
        order.verify(useCase).waitForProducerDelegationState(
                created.getRef(), ProducerDelegationState.ACTIVE
        );
        verify(session).set(originalUser, originalTenant);
    }

    @Test
    void rejectsAsDelegateAndWaitsForRejectedState() {
        journey.setUpProducerDelegation(command(ProducerDelegationState.REJECTED));

        InOrder order = inOrder(session, useCase);
        order.verify(session).set(User.getTenantAdmin(delegate), delegate);
        order.verify(useCase).rejectProducerDelegation(created.getRef());
        order.verify(useCase).waitForProducerDelegationState(
                created.getRef(), ProducerDelegationState.REJECTED
        );
        verify(useCase, never()).approveProducerDelegation(any());
        verify(session).set(originalUser, originalTenant);
    }

    @Test
    void approvesAsDelegateThenRevokesAsDelegator() {
        journey.setUpProducerDelegation(command(ProducerDelegationState.REVOKED));

        InOrder order = inOrder(session, useCase);
        order.verify(session).set(User.getTenantAdmin(delegate), delegate);
        order.verify(useCase).approveProducerDelegation(created.getRef());
        order.verify(useCase).waitForProducerDelegationState(
                created.getRef(), ProducerDelegationState.ACTIVE
        );
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(useCase).revokeProducerDelegation(created.getRef());
        order.verify(useCase).waitForProducerDelegationState(
                created.getRef(), ProducerDelegationState.REVOKED
        );
        verify(session).set(originalUser, originalTenant);
    }

    @Test
    void restoresSessionAndChannelWhenAnOperationFails() {
        RuntimeException failure = new RuntimeException("write failed");
        doThrow(failure).when(useCase).approveProducerDelegation(created.getRef());

        assertThatThrownBy(() -> journey.setUpProducerDelegation(command(ProducerDelegationState.ACTIVE)))
                .isSameAs(failure);

        verify(session).set(originalUser, originalTenant);
        verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }

    @Test
    void restoresContextWhenDelegationCreationFails() {
        RuntimeException failure = new RuntimeException("create failed");
        when(useCase.createProducerDelegation(any(), any(), same(eService))).thenThrow(failure);

        assertThatThrownBy(() -> journey.setUpProducerDelegation(command(ProducerDelegationState.ACTIVE)))
                .isSameAs(failure);

        verify(session).set(originalUser, originalTenant);
        verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }

    private ProducerDelegationSetupCommand command(ProducerDelegationState state) {
        return new ProducerDelegationSetupCommand(eService, delegator, delegate, state);
    }
}
