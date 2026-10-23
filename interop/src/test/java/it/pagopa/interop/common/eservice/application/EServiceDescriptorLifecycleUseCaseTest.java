package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorTransitions;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceDescriptorLifecycleUseCaseTest {
    private final EServiceDescriptorUseCase operations = mock(EServiceDescriptorUseCase.class);
    private final it.pagopa.interop.common.agreement.application.AgreementUseCase agreements =
            mock(it.pagopa.interop.common.agreement.application.AgreementUseCase.class);
    private final EServiceRiskAnalysisUseCase riskAnalyses = mock(EServiceRiskAnalysisUseCase.class);
    private final EServiceDescriptorLifecycleUseCase lifecycle = new EServiceDescriptorLifecycleUseCase(
            operations, new EServiceDescriptorLifecyclePlanner(), agreements, riskAnalyses);
    private final EServiceDescriptor draft = descriptor(DRAFT);
    private final EService eService = EService.builder()
            .id(UUID.randomUUID()).descriptors(new ArrayList<>(List.of(draft))).build();

    @Test
    void preparesPublishesAndSuspendsUsingEveryReturnedDescriptor() {
        EServiceDescriptor prepared = draft.toBuilder().description("prepared").build();
        EServiceDescriptor published = draft.toBuilder().state(PUBLISHED).build();
        EServiceDescriptor suspended = draft.toBuilder().state(SUSPENDED).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(draft);
        when(operations.prepareDescriptorForPublication(
                eService, draft, (UpdateEServiceDescriptorCommand) null
        )).thenReturn(prepared);
        when(operations.publishDescriptor(eService, prepared)).thenReturn(published);
        when(operations.suspendDescriptor(eService, published)).thenReturn(suspended);

        assertThat(lifecycle.bringToState(eService, draft, SUSPENDED)).isSameAs(suspended);

        var order = inOrder(operations, riskAnalyses);
        order.verify(operations).getDescriptor(eService, draft);
        order.verify(operations).prepareDescriptorForPublication(
                eService, draft, (UpdateEServiceDescriptorCommand) null
        );
        order.verify(riskAnalyses).ensureForPublication(eService);
        order.verify(operations).publishDescriptor(eService, prepared);
        order.verify(operations).suspendDescriptor(eService, published);
        order.verifyNoMoreInteractions();
    }

    @Test
    void explicitPreparationAlsoEnsuresRiskAnalysis() {
        when(operations.getDescriptor(eService, draft)).thenReturn(draft);
        when(operations.prepareDescriptorForPublication(eService, draft, (UpdateEServiceDescriptorCommand) null))
                .thenReturn(draft);

        assertThat(lifecycle.prepareForPublication(eService, draft)).isSameAs(draft);

        verify(riskAnalyses).ensureForPublication(eService);
        verify(operations, never()).publishDescriptor(any(), any());
    }

    @Test
    void doesNotPublishWhenRiskAnalysisCannotBeEstablished() {
        when(operations.getDescriptor(eService, draft)).thenReturn(draft);
        when(operations.prepareDescriptorForPublication(eService, draft, (UpdateEServiceDescriptorCommand) null))
                .thenReturn(draft);
        var failure = new IllegalStateException("risk analysis prerequisite failed");
        doThrow(failure).when(riskAnalyses).ensureForPublication(eService);

        assertThatThrownBy(() -> lifecycle.bringToState(eService, draft, PUBLISHED)).isSameAs(failure);

        verify(operations, never()).publishDescriptor(any(), any());
    }

    @Test
    void publishesDelegatedDraftAndPollsUntilItWaitsForProducerApproval() {
        EServiceDescriptor prepared = draft.toBuilder().description("prepared").build();
        EServiceDescriptor waiting = draft.toBuilder().state(WAITING_FOR_APPROVAL).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(draft);
        when(operations.prepareDescriptorForPublication(
                eService, draft, (UpdateEServiceDescriptorCommand) null
        )).thenReturn(prepared);
        when(operations.publishDescriptorAwaitingApproval(eService, prepared)).thenReturn(waiting);

        assertThat(lifecycle.bringToState(eService, draft, WAITING_FOR_APPROVAL)).isSameAs(waiting);

        var order = inOrder(operations, riskAnalyses);
        order.verify(operations).getDescriptor(eService, draft);
        order.verify(operations).prepareDescriptorForPublication(
                eService, draft, (UpdateEServiceDescriptorCommand) null
        );
        order.verify(riskAnalyses).ensureForPublication(eService);
        order.verify(operations).publishDescriptorAwaitingApproval(eService, prepared);
        order.verifyNoMoreInteractions();
    }

    @Test
    void deprecatesPublishedDescriptorByPublishingANextVersionAndPollingTheOriginal() {
        EServiceDescriptor published = draft.toBuilder().state(PUBLISHED).build();
        EService service = eService.toBuilder().descriptors(new ArrayList<>(List.of(published))).build();
        EServiceDescriptor nextDraft = descriptor(DRAFT);
        EServiceDescriptor prepared = nextDraft.toBuilder().description("prepared").build();
        EServiceDescriptor nextPublished = nextDraft.toBuilder().state(PUBLISHED).build();
        EServiceDescriptor deprecated = published.toBuilder().state(DEPRECATED).build();
        when(operations.getDescriptor(service, published)).thenReturn(published);
        when(operations.addDescriptor(service)).thenReturn(nextDraft);
        when(operations.prepareDescriptorForPublication(
                service, nextDraft, (UpdateEServiceDescriptorCommand) null
        )).thenReturn(prepared);
        when(operations.publishDescriptor(service, prepared)).thenReturn(nextPublished);
        when(operations.awaitState(service, published, DEPRECATED)).thenReturn(deprecated);

        assertThat(lifecycle.bringToState(service, published, DEPRECATED)).isSameAs(deprecated);

        var order = inOrder(operations, agreements, riskAnalyses);
        order.verify(operations).getDescriptor(service, published);
        order.verify(agreements).ensureActiveAgreement(service, published);
        order.verify(operations).addDescriptor(service);
        order.verify(operations).prepareDescriptorForPublication(
                service, nextDraft, (UpdateEServiceDescriptorCommand) null
        );
        order.verify(riskAnalyses).ensureForPublication(service);
        order.verify(operations).publishDescriptor(service, prepared);
        order.verify(operations).awaitState(service, published, DEPRECATED);
        order.verifyNoMoreInteractions();
    }

    @Test
    void deprecatesDraftOnlyAfterPublishingItAndTheFollowingVersion() {
        EServiceDescriptor nextDraft = descriptor(DRAFT);
        EServiceDescriptor firstPublished = nextDraft.toBuilder().state(PUBLISHED).build();
        EServiceDescriptor followingDraft = descriptor(DRAFT);
        EServiceDescriptor followingPrepared = followingDraft.toBuilder().description("prepared").build();
        EServiceDescriptor followingPublished = followingDraft.toBuilder().state(PUBLISHED).build();
        EServiceDescriptor deprecated = firstPublished.toBuilder().state(DEPRECATED).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(draft, firstPublished);
        when(operations.prepareDescriptorForPublication(
                eService, draft, (UpdateEServiceDescriptorCommand) null
        )).thenReturn(draft);
        when(operations.publishDescriptor(eService, draft)).thenReturn(firstPublished);
        when(operations.addDescriptor(eService)).thenReturn(followingDraft);
        when(operations.prepareDescriptorForPublication(
                eService, followingDraft, (UpdateEServiceDescriptorCommand) null
        )).thenReturn(followingPrepared);
        when(operations.publishDescriptor(eService, followingPrepared)).thenReturn(followingPublished);
        when(operations.awaitState(eService, firstPublished, DEPRECATED)).thenReturn(deprecated);

        assertThat(lifecycle.bringToState(eService, draft, DEPRECATED)).isSameAs(deprecated);
        var order = inOrder(operations, agreements);
        order.verify(operations).publishDescriptor(eService, draft);
        order.verify(agreements).ensureActiveAgreement(eService, firstPublished);
        order.verify(operations).addDescriptor(eService);
        verify(operations).awaitState(eService, firstPublished, DEPRECATED);
    }

    @Test
    void doesNotPublishNextVersionWhenActiveAgreementCannotBeEstablished() {
        var published = draft.toBuilder().state(PUBLISHED).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(published);
        var failure = new IllegalStateException("agreement prerequisite failed");
        doThrow(failure).when(agreements).ensureActiveAgreement(eService, published);

        assertThatThrownBy(() -> lifecycle.bringToState(eService, draft, DEPRECATED)).isSameAs(failure);

        verify(operations).getDescriptor(eService, draft);
        verifyNoMoreInteractions(operations);
    }

    @ParameterizedTest
    @EnumSource(EServiceDescriptorState.class)
    void doesNotMutateWhenRefreshedDescriptorAlreadyHasRequestedState(EServiceDescriptorState state) {
        EServiceDescriptor refreshed = draft.toBuilder().state(state).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(refreshed);

        assertThat(lifecycle.bringToState(eService, draft, state)).isSameAs(refreshed);

        verify(operations).getDescriptor(eService, draft);
        verifyNoMoreInteractions(operations);
    }

    @ParameterizedTest
    @EnumSource(value = EServiceDescriptorState.class, names = {"PUBLISHED", "DEPRECATED", "ARCHIVING"})
    void suspendsDirectlyToTheExactBusinessResult(EServiceDescriptorState state) {
        EServiceDescriptor current = draft.toBuilder().state(state).build();
        EServiceDescriptorState expected = EServiceDescriptorTransitions.suspensionTarget(state);
        EServiceDescriptor suspended = current.toBuilder().state(expected).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(current);
        when(operations.suspendDescriptor(eService, current)).thenReturn(suspended);

        assertThat(lifecycle.bringToState(eService, draft, expected)).isSameAs(suspended);
        verify(operations).getDescriptor(eService, draft);
        verify(operations).suspendDescriptor(eService, current);
        verifyNoMoreInteractions(operations);
    }

    @Test
    void rejectsSuspendedTargetForAnArchivingVersionWithoutMutation() {
        EServiceDescriptor archiving = draft.toBuilder().state(ARCHIVING).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(archiving);

        assertThatThrownBy(() -> lifecycle.bringToState(eService, draft, SUSPENDED))
                .isInstanceOf(UnsupportedOperationException.class);
        verify(operations).getDescriptor(eService, draft);
        verifyNoMoreInteractions(operations);
    }

    @Test
    void schedulesArchivingAndRefreshesStateBeforeSuspending() {
        EServiceDescriptor published = draft.toBuilder().state(PUBLISHED).build();
        EServiceDescriptor archiving = draft.toBuilder().state(ARCHIVING).build();
        EServiceDescriptor suspended = draft.toBuilder().state(ARCHIVING_SUSPENDED).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(published);
        when(operations.getDescriptor(eService, published)).thenReturn(archiving);
        when(operations.suspendDescriptor(eService, archiving)).thenReturn(suspended);

        assertThat(lifecycle.bringToState(eService, draft, ARCHIVING_SUSPENDED)).isSameAs(suspended);

        var order = inOrder(operations);
        order.verify(operations).getDescriptor(eService, draft);
        order.verify(operations).archiveDescriptor(eService, published, GracePeriodDays.NUMBER_60);
        order.verify(operations).getDescriptor(eService, published);
        order.verify(operations).suspendDescriptor(eService, archiving);
        order.verifyNoMoreInteractions();
    }

    @Test
    void failsWhenOperationReturnsAnUnexpectedState() {
        EServiceDescriptor published = draft.toBuilder().state(PUBLISHED).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(published);
        when(operations.suspendDescriptor(eService, published)).thenReturn(published);

        assertThatThrownBy(() -> lifecycle.bringToState(eService, draft, SUSPENDED))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Expected descriptor state SUSPENDED");
    }

    @ParameterizedTest
    @EnumSource(value = EServiceDescriptorState.class, names = {"DRAFT", "SUSPENDED", "ARCHIVED", "WAITING_FOR_APPROVAL", "ARCHIVING_SUSPENDED"})
    void rejectsInvalidDirectSuspension(EServiceDescriptorState state) {
        assertThatThrownBy(() -> EServiceDescriptorTransitions.suspensionTarget(state))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updatesADraftOnceWithExplicitConfiguration() {
        UpdateEServiceDescriptorCommand command = mock(UpdateEServiceDescriptorCommand.class);
        EServiceDescriptor updated = draft.toBuilder().description("configured").build();
        when(operations.getDescriptor(eService, draft)).thenReturn(draft);
        when(operations.updateDescriptor(eService, draft, command)).thenReturn(updated);

        assertThat(lifecycle.bringToState(eService, draft, DRAFT, command)).isSameAs(updated);
        verify(operations).getDescriptor(eService, draft);
        verify(operations).updateDescriptor(eService, draft, command);
        verifyNoMoreInteractions(operations);
    }

    @Test
    void plansApprovalStateWithoutSilentlyPerformingTheDelegation() {
        assertThatCode(() -> lifecycle.validateCanReachState(DRAFT, WAITING_FOR_APPROVAL))
                .doesNotThrowAnyException();
        verifyNoInteractions(operations);
    }

    @Test
    void rejectsTerminalArchiveSetupWhenOnlyTheScheduledTransitionIsAvailable() {
        assertThatThrownBy(() -> lifecycle.validateCanReachState(ARCHIVING, ARCHIVED))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Unsupported setup path: ARCHIVING -> ARCHIVED");
        verifyNoInteractions(operations);
    }

    @Test
    void stopsThePlanIfPublicationDoesNotReachItsExpectedState() {
        when(operations.getDescriptor(eService, draft)).thenReturn(draft);
        when(operations.prepareDescriptorForPublication(
                eService, draft, (UpdateEServiceDescriptorCommand) null)).thenReturn(draft);
        when(operations.publishDescriptor(eService, draft)).thenReturn(draft);

        assertThatThrownBy(() -> lifecycle.bringToState(eService, draft, SUSPENDED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Expected descriptor state PUBLISHED but was DRAFT");

        verify(operations, never()).suspendDescriptor(any(), any());
    }

    @ParameterizedTest
    @EnumSource(value = EServiceDescriptorState.class, names = {"PUBLISHED", "DEPRECATED", "ARCHIVING"})
    void rejectsDraftConfigurationBeforeExecutingSuspension(EServiceDescriptorState source) {
        EServiceDescriptor current = draft.toBuilder().state(source).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(current);

        assertThatThrownBy(() -> lifecycle.bringToState(eService, draft,
                EServiceDescriptorTransitions.suspensionTarget(source), mock(UpdateEServiceDescriptorCommand.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot apply draft configuration in state " + source);

        verify(operations).getDescriptor(eService, draft);
        verifyNoMoreInteractions(operations);
    }

    @Test
    void passesExplicitConfigurationOnlyToPublication() {
        UpdateEServiceDescriptorCommand command = mock(UpdateEServiceDescriptorCommand.class);
        EServiceDescriptor prepared = draft.toBuilder().description("configured").build();
        EServiceDescriptor published = draft.toBuilder().state(PUBLISHED).build();
        EServiceDescriptor suspended = draft.toBuilder().state(SUSPENDED).build();
        when(operations.getDescriptor(eService, draft)).thenReturn(draft);
        when(operations.prepareDescriptorForPublication(eService, draft, command)).thenReturn(prepared);
        when(operations.publishDescriptor(eService, prepared)).thenReturn(published);
        when(operations.suspendDescriptor(eService, published)).thenReturn(suspended);

        assertThat(lifecycle.bringToState(eService, draft, SUSPENDED, command)).isSameAs(suspended);

        verify(operations, never()).updateDescriptor(any(), any(), any());
        verify(operations).prepareDescriptorForPublication(eService, draft, command);
    }

    private static EServiceDescriptor descriptor(EServiceDescriptorState state) {
        return EServiceDescriptor.builder().id(UUID.randomUUID()).version("1").state(state).build();
    }
}
