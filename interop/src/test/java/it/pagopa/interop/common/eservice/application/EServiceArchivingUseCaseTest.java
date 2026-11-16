package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceArchivingUseCaseTest {
    private final EServiceUseCase services = mock(EServiceUseCase.class);
    private final EServiceDescriptorUseCase descriptors = mock(EServiceDescriptorUseCase.class);
    private final EServiceArchivingUseCase archiving = new EServiceArchivingUseCase(services, descriptors);

    @ParameterizedTest
    @CsvSource({"PUBLISHED,ARCHIVING", "SUSPENDED,ARCHIVING_SUSPENDED"})
    void archivesTheEntireServiceIncludingItsOnlyLatestVersion(
            EServiceDescriptorState source, EServiceDescriptorState expected) {
        var version = descriptor(1, source);
        var service = service(version);
        when(descriptors.getDescriptor(service, version)).thenReturn(version);
        when(descriptors.awaitState(service, version, expected))
                .thenReturn(version.toBuilder().state(expected).build());

        archiving.archiveEntireEService(service);

        var order = inOrder(services, descriptors);
        order.verify(descriptors).getDescriptor(service, version);
        order.verify(services).archiveEService(service, GracePeriodDays.NUMBER_60);
        order.verify(descriptors).awaitState(service, version, expected);
        order.verifyNoMoreInteractions();
        assertThat(version.getState()).isEqualTo(source);
    }

    @Test
    void refreshesAllVersionsAndWaitsForEveryPostconditionWithoutArchivingIndividually() {
        var deprecated = descriptor(1, DEPRECATED);
        var suspended = descriptor(2, SUSPENDED);
        var archived = descriptor(3, ARCHIVED);
        var alreadyArchiving = descriptor(4, ARCHIVING);
        var alreadySuspended = descriptor(5, ARCHIVING_SUSPENDED);
        var latest = descriptor(6, PUBLISHED);
        var staleLatest = latest.toBuilder().state(DRAFT).build();
        var service = service(staleLatest, suspended, archived, deprecated, alreadyArchiving, alreadySuspended);
        when(descriptors.getDescriptor(service, staleLatest)).thenReturn(latest);
        for (var version : List.of(suspended, archived, deprecated, alreadyArchiving, alreadySuspended)) {
            when(descriptors.getDescriptor(service, version)).thenReturn(version);
        }
        when(descriptors.awaitState(service, latest, ARCHIVING))
                .thenReturn(latest.toBuilder().state(ARCHIVING).build());
        when(descriptors.awaitState(service, deprecated, ARCHIVING))
                .thenReturn(deprecated.toBuilder().state(ARCHIVING).build());
        when(descriptors.awaitState(service, suspended, ARCHIVING_SUSPENDED))
                .thenReturn(suspended.toBuilder().state(ARCHIVING_SUSPENDED).build());
        when(descriptors.awaitState(service, archived, ARCHIVED)).thenReturn(archived);
        when(descriptors.awaitState(service, alreadyArchiving, ARCHIVING)).thenReturn(alreadyArchiving);
        when(descriptors.awaitState(service, alreadySuspended, ARCHIVING_SUSPENDED)).thenReturn(alreadySuspended);

        archiving.archiveEntireEService(service);

        verify(services).archiveEService(service, GracePeriodDays.NUMBER_60);
        verify(descriptors).awaitState(service, latest, ARCHIVING);
        verify(descriptors).awaitState(service, deprecated, ARCHIVING);
        verify(descriptors).awaitState(service, suspended, ARCHIVING_SUSPENDED);
        verify(descriptors).awaitState(service, archived, ARCHIVED);
        verify(descriptors).awaitState(service, alreadyArchiving, ARCHIVING);
        verify(descriptors).awaitState(service, alreadySuspended, ARCHIVING_SUSPENDED);
        verify(descriptors, never()).archiveDescriptor(any(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(value = EServiceDescriptorState.class, names = {"DRAFT", "WAITING_FOR_APPROVAL"})
    void rejectsInactiveVersionsBeforeSchedulingOrDeletingAnything(EServiceDescriptorState state) {
        var version = descriptor(2, state);
        var service = service(version);
        when(descriptors.getDescriptor(service, version)).thenReturn(version);

        assertThatThrownBy(() -> archiving.archiveEntireEService(service))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("unsupported state " + state);

        verifyNoInteractions(services);
        verify(descriptors).getDescriptor(service, version);
        verifyNoMoreInteractions(descriptors);
    }

    @Test
    void rejectsALatestVersionThatCannotStartWholeServiceArchiving() {
        var version = descriptor(1, ARCHIVING);
        var service = service(version);
        when(descriptors.getDescriptor(service, version)).thenReturn(version);

        assertThatThrownBy(() -> archiving.archiveEntireEService(service))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("latest version");
        verifyNoInteractions(services);
        verify(descriptors, never()).awaitState(any(), any(), any());
    }

    @Test
    void reportsAnUnexpectedStateReturnedByTheGateway() {
        var version = descriptor(1, PUBLISHED);
        var service = service(version);
        when(descriptors.getDescriptor(service, version)).thenReturn(version);
        when(descriptors.awaitState(service, version, ARCHIVING)).thenReturn(version);

        assertThatThrownBy(() -> archiving.archiveEntireEService(service))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("in state ARCHIVING but was PUBLISHED");
    }

    @Test
    void rejectsAnEmptyService() {
        assertThatThrownBy(() -> archiving.archiveEntireEService(service()))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("without descriptors");
        verifyNoInteractions(services, descriptors);
    }

    @Test
    void propagatesSchedulingFailureWithoutStartingStateWaits() {
        var version = descriptor(1, PUBLISHED);
        var service = service(version);
        when(descriptors.getDescriptor(service, version)).thenReturn(version);
        var failure = new IllegalStateException("Archiving rejected");
        doThrow(failure).when(services).archiveEService(service, GracePeriodDays.NUMBER_60);

        assertThatThrownBy(() -> archiving.archiveEntireEService(service)).isSameAs(failure);
        verify(descriptors, never()).awaitState(any(), any(), any());
    }

    private EService service(EServiceDescriptor... versions) {
        return EService.builder().id(UUID.randomUUID()).descriptors(List.of(versions)).build();
    }

    private EServiceDescriptor descriptor(int version, EServiceDescriptorState state) {
        return EServiceDescriptor.builder().id(UUID.randomUUID()).version(Integer.toString(version))
                .state(state).build();
    }
}
