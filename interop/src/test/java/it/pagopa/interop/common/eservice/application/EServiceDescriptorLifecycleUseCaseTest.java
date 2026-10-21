package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorTransitions;
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
    private final EServiceDescriptorLifecycleUseCase lifecycle = new EServiceDescriptorLifecycleUseCase(
            operations, new EServiceDescriptorLifecyclePlanner());
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

        var order = inOrder(operations);
        order.verify(operations).getDescriptor(eService, draft);
        order.verify(operations).prepareDescriptorForPublication(
                eService, draft, (UpdateEServiceDescriptorCommand) null
        );
        order.verify(operations).publishDescriptor(eService, prepared);
        order.verify(operations).suspendDescriptor(eService, published);
        order.verifyNoMoreInteractions();
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
    void rejectsUnsupportedSetupBeforeCreatingAVersion() {
        assertThatThrownBy(() -> lifecycle.validateCanReachState(DRAFT, DEPRECATED))
                .isInstanceOf(UnsupportedOperationException.class);
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
