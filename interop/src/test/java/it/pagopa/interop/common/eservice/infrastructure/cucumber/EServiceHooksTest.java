package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
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
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    @SuppressWarnings("unchecked")
    private final CurrentChannel<Channel> channel = mock(CurrentChannel.class);
    private final EServiceHooks hooks = new EServiceHooks(store, services, session, channel);

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
    @EnumSource(value = EServiceDescriptorState.class, mode = EnumSource.Mode.EXCLUDE, names = "DRAFT")
    void archivesNonDraftEServicesWithSixtyDaysGrace(EServiceDescriptorState state) {
        EService service = eService(state);
        when(store.getAll(EService.class)).thenReturn(List.of(service));

        hooks.cleanUpEServices();

        verify(services).archiveEService(service, GracePeriodDays.NUMBER_60);
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
