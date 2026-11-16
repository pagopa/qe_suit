package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static it.pagopa.interop.common.agreement.domain.AgreementState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgreementJourneyImplTest {
    private final AgreementUseCase agreements = mock(AgreementUseCase.class);
    private final EntityStore entityStore = mock(EntityStore.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    private final CurrentChannel<Channel> channel = mock(CurrentChannel.class);
    private final AgreementJourneyImpl journey =
            new AgreementJourneyImpl(agreements, entityStore, session, channel);
    private final EService eService = EService.builder()
            .id(UUID.randomUUID())
            .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString())
            .descriptors(java.util.List.of(EServiceDescriptor.builder()
                    .id(UUID.randomUUID()).version("1").state(EServiceDescriptorState.PUBLISHED).build()))
            .build();
    private final Agreement draft = agreement(DRAFT);

    @Test
    void keepsDraftWithoutSubmitting() {
        when(agreements.createAgreement(eService, eService.getActiveDescriptor(), null)).thenReturn(draft);

        assertThat(journey.linkAgreement(eService, DRAFT, null)).isSameAs(journey);

        verify(agreements, never()).submitAgreement(any());
        verifyNoInteractions(session, channel);
    }

    @Test
    void preservesPendingWhenSubmissionRequiresProducerApproval() {
        Agreement pending = agreement(PENDING);
        stubSubmission(pending);

        assertThat(journey.linkAgreement(eService, PENDING, null)).isSameAs(journey);

        verify(agreements, never()).activateAgreement(any(), any());
        verifyNoInteractions(session, channel);
    }

    @Test
    void activatesPendingAsProducerAndRestoresTheOriginalSessionAndChannel() {
        Agreement pending = agreement(PENDING);
        Agreement active = agreement(ACTIVE);
        stubSubmission(pending);
        when(session.getUser()).thenReturn(User.getTenantAdmin(Tenant.PAGO_PA));
        when(session.getTenant()).thenReturn(Tenant.PAGO_PA);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);
        when(agreements.activateAgreement(pending, null)).thenReturn(active);
        when(agreements.awaitState(active, Set.of(ACTIVE))).thenReturn(active);

        assertThat(journey.linkAgreement(eService, ACTIVE, null)).isSameAs(journey);

        var order = inOrder(channel, session, agreements);
        order.verify(channel).setCurrentChannel(Channel.BFF);
        order.verify(session).set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
        order.verify(agreements).activateAgreement(pending, null);
        order.verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
        order.verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }

    @Test
    void acceptsAutomaticActivationWithoutCallingTheProducerApprovalOperation() {
        Agreement active = agreement(ACTIVE);
        stubSubmission(active);

        assertThat(journey.linkAgreement(eService, ACTIVE, null)).isSameAs(journey);

        verify(agreements, never()).activateAgreement(any(), any());
        verifyNoInteractions(session, channel);
    }

    @Test
    void suspendsAnActiveAgreementThroughTheConsumerOperation() {
        Agreement active = agreement(ACTIVE);
        Agreement suspended = agreement(SUSPENDED);
        stubSubmission(active);
        when(agreements.suspendAgreement(active, null)).thenReturn(suspended);
        when(agreements.awaitState(suspended, Set.of(SUSPENDED))).thenReturn(suspended);

        assertThat(journey.linkAgreement(eService, SUSPENDED, null)).isSameAs(journey);

        verify(agreements).suspendAgreement(active, null);
        verifyNoInteractions(session, channel);
    }

    @Test
    void archivesAnActiveAgreementThroughTheSupportedArchiveOperation() {
        Agreement active = agreement(ACTIVE);
        Agreement archived = agreement(ARCHIVED);
        stubSubmission(active);
        when(agreements.archiveAgreement(active)).thenReturn(archived);
        when(agreements.awaitState(archived, Set.of(ARCHIVED))).thenReturn(archived);

        assertThat(journey.linkAgreement(eService, ARCHIVED, null)).isSameAs(journey);

        verify(agreements).archiveAgreement(active);
        verifyNoInteractions(session, channel);
    }

    @Test
    void reportsAnAutomaticallyActivatedAgreementWhenPendingWasRequested() {
        stubSubmission(agreement(ACTIVE));

        assertThatThrownBy(() -> journey.linkAgreement(eService, PENDING, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("instead of PENDING");
    }

    @Test
    void preservesActivationFailureAndStillAttemptsBothSessionRestorations() {
        Agreement pending = agreement(PENDING);
        RuntimeException activationFailure = new IllegalStateException("approval failed");
        RuntimeException userRestoreFailure = new IllegalStateException("user restore failed");
        stubSubmission(pending);
        when(session.getUser()).thenReturn(User.getTenantAdmin(Tenant.PAGO_PA));
        when(session.getTenant()).thenReturn(Tenant.PAGO_PA);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);
        when(agreements.activateAgreement(pending, null)).thenThrow(activationFailure);
        doThrow(userRestoreFailure).when(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);

        assertThatThrownBy(() -> journey.linkAgreement(eService, ACTIVE, null))
                .isSameAs(activationFailure)
                .satisfies(failure -> assertThat(failure.getSuppressed()).containsExactly(userRestoreFailure));

        verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }

    @Test
    void attemptsChannelRestorationWhenUserRestorationFailsAfterSuccessfulActivation() {
        Agreement pending = agreement(PENDING);
        Agreement active = agreement(ACTIVE);
        RuntimeException userRestoreFailure = new IllegalStateException("user restore failed");
        stubSubmission(pending);
        when(session.getUser()).thenReturn(User.getTenantAdmin(Tenant.PAGO_PA));
        when(session.getTenant()).thenReturn(Tenant.PAGO_PA);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);
        when(agreements.activateAgreement(pending, null)).thenReturn(active);
        doThrow(userRestoreFailure).when(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);

        assertThatThrownBy(() -> journey.linkAgreement(eService, ACTIVE, null))
                .isSameAs(userRestoreFailure);

        verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
        verify(agreements, never()).awaitState(active, Set.of(ACTIVE));
    }

    private void stubSubmission(Agreement submitted) {
        when(agreements.createAgreement(eService, eService.getActiveDescriptor(), null)).thenReturn(draft);
        when(agreements.submitAgreement(draft)).thenReturn(submitted);
        when(agreements.awaitState(submitted, Set.of(PENDING, ACTIVE))).thenReturn(submitted);
    }

    private Agreement agreement(AgreementState state) {
        return Agreement.builder()
                .id(UUID.randomUUID())
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId())
                .consumerId(Tenant.PAGO_PA.getOrganizationId())
                .state(state)
                .build();
    }
}
