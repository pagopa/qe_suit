package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorApprovalUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceHooksTest {
    private final EntityStore store = mock(EntityStore.class);
    private final EServiceUseCase services = mock(EServiceUseCase.class);
    private final EServiceDescriptorApprovalUseCase descriptors = mock(EServiceDescriptorApprovalUseCase.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    @SuppressWarnings("unchecked")
    private final CurrentChannel<Channel> channel = mock(CurrentChannel.class);
    private final EServiceHooks hooks = new EServiceHooks(store, services, descriptors, session, channel);

    @Test
    void deletesDraftEServiceAsProducerThroughBffAndRestoresContext() {
        EService draft = eService(DRAFT);
        User previousUser = mock(User.class);
        when(store.getAll(EService.class)).thenReturn(List.of(draft));
        when(session.getUser()).thenReturn(previousUser);
        when(session.getTenant()).thenReturn(Tenant.AGID);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);

        hooks.cleanUpEServices();

        var order = inOrder(channel, session, services);
        order.verify(channel).setCurrentChannel(Channel.BFF);
        order.verify(session).set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
        order.verify(services).deleteEService(draft);
        order.verify(session).set(previousUser, Tenant.AGID);
        order.verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
        verifyNoMoreInteractions(services);
    }

    @ParameterizedTest
    @EnumSource(value = EServiceDescriptorState.class, names = {"PUBLISHED", "SUSPENDED", "DEPRECATED"})
    void archivesNonDraftEServicesWithSixtyDaysGrace(EServiceDescriptorState state) {
        EService service = eService(state);
        when(store.getAll(EService.class)).thenReturn(List.of(service));

        hooks.cleanUpEServices();

        verify(services).archiveEService(service, GracePeriodDays.NUMBER_60);
        verifyNoMoreInteractions(services);
    }

    @Test
    void doesNotScheduleArchivingAgainWhenEveryVersionIsRetiringOrArchivedAndRestoresContext() {
        EService service = eService(ARCHIVING, ARCHIVING_SUSPENDED, ARCHIVED);
        User previousUser = mock(User.class);
        when(store.getAll(EService.class)).thenReturn(List.of(service));
        when(session.getUser()).thenReturn(previousUser);
        when(session.getTenant()).thenReturn(Tenant.AGID);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);

        hooks.cleanUpEServices();

        verifyNoInteractions(services);
        verify(session).set(previousUser, Tenant.AGID);
        verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }

    @ParameterizedTest
    @EnumSource(value = EServiceDescriptorState.class,
            names = {"ARCHIVING", "ARCHIVING_SUSPENDED", "ARCHIVED"})
    void skipsASingleVersionAlreadyRetiringOrArchived(EServiceDescriptorState state) {
        when(store.getAll(EService.class)).thenReturn(List.of(eService(state)));

        hooks.cleanUpEServices();

        verifyNoInteractions(services);
    }

    @ParameterizedTest
    @EnumSource(value = EServiceDescriptorState.class, names = {"PUBLISHED", "SUSPENDED"})
    void stillArchivesTheWholeServiceWhenOnlyOlderVersionsAreRetiring(EServiceDescriptorState state) {
        EService service = eService(ARCHIVED, ARCHIVING, ARCHIVING_SUSPENDED, state);
        when(store.getAll(EService.class)).thenReturn(List.of(service));

        hooks.cleanUpEServices();

        verify(services).archiveEService(service, GracePeriodDays.NUMBER_60);
        verifyNoMoreInteractions(services);
    }

    @Test
    void continuesCleaningOtherServicesAfterSkippingAnAlreadyRetiringService() {
        EService retiring = eService(ARCHIVING);
        EService draft = eService(DRAFT);
        when(store.getAll(EService.class)).thenReturn(List.of(retiring, draft));

        hooks.cleanUpEServices();

        verify(services).deleteEService(draft);
        verifyNoMoreInteractions(services);
    }

    @Test
    void archivesInsteadOfDeletingWhenANewDraftCoexistsWithAPublishedVersion() {
        EService service = eService(PUBLISHED, DRAFT);
        when(store.getAll(EService.class)).thenReturn(List.of(service));

        hooks.cleanUpEServices();

        verify(services).archiveEService(service, GracePeriodDays.NUMBER_60);
        verifyNoMoreInteractions(services);
    }

    @Test
    void rejectsTheOnlyPendingVersionBeforeDeletingTheUnpublishedService() {
        EService service = eService(WAITING_FOR_APPROVAL);
        var pending = service.getDescriptors().get(0);
        var draft = pending.toBuilder().state(DRAFT).build();
        when(store.getAll(EService.class)).thenReturn(List.of(service));
        when(descriptors.rejectDescriptor(service, pending, "Cleanup dello scenario")).thenReturn(draft);

        hooks.cleanUpEServices();

        var order = inOrder(descriptors, services);
        order.verify(descriptors).rejectDescriptor(service, pending, "Cleanup dello scenario");
        order.verify(services).deleteEService(service);
        verifyNoMoreInteractions(services, descriptors);
    }

    @Test
    void rejectsPendingSuccessorBeforeArchivingThePublishedService() {
        EService service = eService(PUBLISHED, WAITING_FOR_APPROVAL);
        var pending = service.getDescriptors().get(1);
        when(store.getAll(EService.class)).thenReturn(List.of(service));
        when(descriptors.rejectDescriptor(service, pending, "Cleanup dello scenario"))
                .thenReturn(pending.toBuilder().state(DRAFT).build());

        hooks.cleanUpEServices();

        var order = inOrder(descriptors, services);
        order.verify(descriptors).rejectDescriptor(service, pending, "Cleanup dello scenario");
        order.verify(services).archiveEService(service, GracePeriodDays.NUMBER_60);
        verifyNoMoreInteractions(services, descriptors);
    }

    @Test
    void doesNotAttemptDeletionOrArchivingAfterRejectionFailsButContinuesCleanup() {
        EService pendingService = eService(WAITING_FOR_APPROVAL);
        EService draft = eService(DRAFT);
        var failure = new IllegalStateException("rejection state wait timed out");
        when(store.getAll(EService.class)).thenReturn(List.of(pendingService, draft));
        when(descriptors.rejectDescriptor(eq(pendingService), any(), anyString())).thenThrow(failure);

        assertThatThrownBy(hooks::cleanUpEServices)
                .satisfies(error -> assertThat(error.getSuppressed()).containsExactly(failure));

        verify(services).deleteEService(draft);
        verifyNoMoreInteractions(services);
        verify(session).set(null, null);
        verify(channel).setCurrentChannel(null);
    }

    @Test
    void attemptsRemainingCleanupsAndRestoresContextAfterFailures() {
        EService draft = eService(DRAFT);
        EService published = eService(PUBLISHED);
        RuntimeException deleteFailure = new IllegalStateException("delete failed");
        AssertionError archiveFailure = new AssertionError("archive failed");
        when(store.getAll(EService.class)).thenReturn(List.of(draft, published));
        User previousUser = mock(User.class);
        when(session.getUser()).thenReturn(previousUser);
        when(session.getTenant()).thenReturn(Tenant.AGID);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);
        doThrow(deleteFailure).when(services).deleteEService(draft);
        doThrow(archiveFailure).when(services).archiveEService(published, GracePeriodDays.NUMBER_60);

        assertThatThrownBy(hooks::cleanUpEServices)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cleanup degli e-service non completato")
                .satisfies(error -> assertThat(error.getSuppressed()).containsExactly(deleteFailure, archiveFailure));

        verify(services).deleteEService(draft);
        verify(services).archiveEService(published, GracePeriodDays.NUMBER_60);
        verify(session).set(previousUser, Tenant.AGID);
        verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }

    @Test
    void doesNothingWhenNoEServicesAreStored() {
        when(store.getAll(EService.class)).thenReturn(List.of());

        hooks.cleanUpEServices();

        verifyNoInteractions(services, session, channel);
    }

    private EService eService(EServiceDescriptorState... states) {
        return EService.builder().id(UUID.randomUUID())
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString())
                .descriptors(Arrays.stream(states).map(state -> EServiceDescriptor.builder()
                        .id(UUID.randomUUID()).state(state).build()).toList())
                .build();
    }
}
