package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.context.CucumberEntityStore;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.delegation.domain.ProducerDelegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceHooks;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProducerDelegationHooksTest {

    private final CucumberEntityStore store = new CucumberEntityStore();
    private final DelegationUseCase useCase = mock(DelegationUseCase.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    private final CurrentChannel<Channel> channel = mock(CurrentChannel.class);
    private final ProducerDelegationHooks hooks = new ProducerDelegationHooks(store, useCase, session, channel);
    private final Tenant delegator = Tenant.COMUNE_DI_MILANO;
    private final Tenant delegate = Tenant.COMUNE_DI_POZZALLO;
    private final Tenant originalTenant = Tenant.COMUNE_DI_COMUN_NUOVO;
    private final User originalUser = User.getTenantAdmin(originalTenant);

    @BeforeEach
    void setUpSession() {
        when(session.getUser()).thenReturn(originalUser);
        when(session.getTenant()).thenReturn(originalTenant);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);
    }

    @Test
    void excludesPreexistingIdsEvenWhenTheirSnapshotsAreReplaced() {
        ProducerDelegation preexisting = delegation(1, ProducerDelegationState.ACTIVE);
        store.upsert(preexisting);
        hooks.snapshotProducerDelegations();
        store.upsert(preexisting.toBuilder().state(ProducerDelegationState.WAITING_FOR_APPROVAL).build());
        ProducerDelegation created = register(2, ProducerDelegationState.ACTIVE);

        hooks.cleanUpProducerDelegations();

        verify(useCase, never()).getProducerDelegation(preexisting.getRef());
        verify(useCase).revokeProducerDelegation(created.getRef());
        verifyRestored();
    }

    @Test
    void readsAsStoredDelegatorBeforeRevokingFreshActiveState() {
        hooks.snapshotProducerDelegations();
        ProducerDelegation created = register(1, ProducerDelegationState.WAITING_FOR_APPROVAL);
        when(useCase.getProducerDelegation(created.getRef()))
                .thenReturn(created.toBuilder().state(ProducerDelegationState.ACTIVE).build());

        hooks.cleanUpProducerDelegations();

        var order = inOrder(channel, session, useCase);
        order.verify(channel).setCurrentChannel(Channel.BFF);
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(useCase).getProducerDelegation(created.getRef());
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(useCase).revokeProducerDelegation(created.getRef());
        order.verify(useCase).waitForProducerDelegationState(created.getRef(), ProducerDelegationState.REVOKED);
        order.verify(session).set(originalUser, originalTenant);
        order.verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
        verify(useCase, never()).rejectProducerDelegation(any());
    }

    @Test
    void readsAsDelegatorThenRejectsPendingDelegationAsDelegate() {
        hooks.snapshotProducerDelegations();
        ProducerDelegation created = register(1, ProducerDelegationState.WAITING_FOR_APPROVAL);

        hooks.cleanUpProducerDelegations();

        var order = inOrder(session, useCase);
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(useCase).getProducerDelegation(created.getRef());
        order.verify(session).set(User.getTenantAdmin(delegate), delegate);
        order.verify(useCase).rejectProducerDelegation(created.getRef());
        order.verify(useCase).waitForProducerDelegationState(created.getRef(), ProducerDelegationState.REJECTED);
        verify(useCase, never()).revokeProducerDelegation(any());
        verifyRestored();
    }

    @Test
    void doesNotMutateTerminalDelegations() {
        hooks.snapshotProducerDelegations();
        ProducerDelegation rejected = register(1, ProducerDelegationState.REJECTED);
        ProducerDelegation revoked = register(2, ProducerDelegationState.REVOKED);

        hooks.cleanUpProducerDelegations();

        verify(useCase).getProducerDelegation(rejected.getRef());
        verify(useCase).getProducerDelegation(revoked.getRef());
        verifyNoMoreInteractions(useCase);
        verifyRestored();
    }

    @Test
    void aggregatesReadAndTransitionFailuresContinuesCleanupAndRestoresContext() {
        hooks.snapshotProducerDelegations();
        ProducerDelegation unreadable = register(1, ProducerDelegationState.ACTIVE);
        ProducerDelegation pending = register(2, ProducerDelegationState.WAITING_FOR_APPROVAL);
        ProducerDelegation active = register(3, ProducerDelegationState.ACTIVE);
        var readFailure = new IllegalStateException("read failed");
        var transitionFailure = new AssertionError("state not reached");
        when(useCase.getProducerDelegation(unreadable.getRef())).thenThrow(readFailure);
        doThrow(transitionFailure).when(useCase)
                .waitForProducerDelegationState(pending.getRef(), ProducerDelegationState.REJECTED);

        assertThatThrownBy(hooks::cleanUpProducerDelegations)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cleanup delle deleghe di erogazione non completato")
                .satisfies(failure -> assertThat(failure.getSuppressed())
                        .containsExactly(readFailure, transitionFailure));

        var order = inOrder(session, useCase);
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(useCase).getProducerDelegation(unreadable.getRef());
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(useCase).getProducerDelegation(pending.getRef());
        order.verify(session).set(User.getTenantAdmin(delegate), delegate);
        order.verify(useCase).rejectProducerDelegation(pending.getRef());
        order.verify(useCase).waitForProducerDelegationState(pending.getRef(), ProducerDelegationState.REJECTED);
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(useCase).getProducerDelegation(active.getRef());
        order.verify(useCase).revokeProducerDelegation(active.getRef());
        order.verify(useCase).waitForProducerDelegationState(active.getRef(), ProducerDelegationState.REVOKED);
        verifyRestored();
    }

    @Test
    void doesNothingWithoutNewResourcesOrSuccessfulBoundarySnapshot() {
        store.upsert(delegation(1, ProducerDelegationState.ACTIVE));
        hooks.cleanUpProducerDelegations();
        hooks.snapshotProducerDelegations();
        hooks.cleanUpProducerDelegations();

        verifyNoInteractions(useCase);
        verify(session, never()).set(any(), any());
        verify(channel, never()).setCurrentChannel(any());
    }

    @Test
    void snapshotsAtScenarioBoundaryAndCleansUpBeforeEServices() throws NoSuchMethodException {
        Before before = ProducerDelegationHooks.class.getMethod("snapshotProducerDelegations")
                .getAnnotation(Before.class);
        After after = ProducerDelegationHooks.class.getMethod("cleanUpProducerDelegations")
                .getAnnotation(After.class);
        After eserviceAfter = EServiceHooks.class.getMethod("cleanUpEServices").getAnnotation(After.class);

        assertThat(before.value()).isEqualTo("@private-catalog");
        assertThat(after.value()).isEqualTo("@private-catalog");
        assertThat(after.order()).isGreaterThan(eserviceAfter.order());
    }

    private ProducerDelegation register(int id, ProducerDelegationState state) {
        ProducerDelegation delegation = delegation(id, state);
        store.upsert(delegation);
        when(useCase.getProducerDelegation(delegation.getRef())).thenReturn(delegation);
        return delegation;
    }

    private ProducerDelegation delegation(int id, ProducerDelegationState state) {
        return ProducerDelegation.builder()
                .id(new UUID(0, id))
                .delegatorId(delegator.getOrganizationId())
                .delegateId(delegate.getOrganizationId())
                .eServiceId(new UUID(1, id))
                .state(state)
                .build();
    }

    private void verifyRestored() {
        verify(session).set(originalUser, originalTenant);
        verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }
}
