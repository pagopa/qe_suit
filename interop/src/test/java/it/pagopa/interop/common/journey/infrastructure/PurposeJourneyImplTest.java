package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PurposeJourneyImplTest {

    private final PurposeUseCase purposeUseCase = mock(PurposeUseCase.class);
    private final EntityStore entityStore = mock(EntityStore.class);
    @SuppressWarnings("unchecked")
    private final CurrentChannel<Channel> currentChannel = mock(CurrentChannel.class);
    private final AuthSession currentUserSession = mock(AuthSession.class);
    private final PurposeJourneyImpl journey =
            new PurposeJourneyImpl(purposeUseCase, entityStore, currentUserSession, currentChannel);
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;
    private final Tenant consumer = Tenant.COMUNE_DI_POZZALLO;
    private final EService eService = EService.builder()
            .id(UUID.randomUUID())
            .producerId(producer.getOrganizationId().toString())
            .build();
    private final PurposeVersion draftVersion = PurposeVersion.builder()
            .id(UUID.randomUUID())
            .purposeVersionState(PurposeVersionState.DRAFT)
            .createdAt(Instant.EPOCH)
            .build();
    private final Purpose draftPurpose = Purpose.builder()
            .id(UUID.randomUUID())
            .version(draftVersion)
            .build();

    @Test
    void activatesThenArchivesThePurposeWhenArchivedStateIsRequested() {
        PurposeVersion activeVersion = draftVersion.toBuilder()
                .purposeVersionState(PurposeVersionState.ACTIVE)
                .build();
        Purpose activePurpose = draftPurpose.toBuilder().clearVersions().version(activeVersion).build();
        when(purposeUseCase.createPurpose(eService)).thenReturn(draftPurpose);
        when(purposeUseCase.activatePurpose(draftPurpose, draftVersion, null)).thenReturn(activePurpose);

        journey.createPurposeAndLinkToEService(eService, PurposeVersionState.ARCHIVED);

        InOrder order = inOrder(purposeUseCase);
        order.verify(purposeUseCase).createPurpose(eService);
        order.verify(purposeUseCase).activatePurpose(draftPurpose, draftVersion, null);
        order.verify(purposeUseCase).archivePurpose(activePurpose, activeVersion);
        verifyNoMoreInteractions(purposeUseCase);
    }

    @Test
    void rejectsThePurposeWithTheProducerSessionAndRestoresTheConsumerSession() {
        PurposeVersion waitingVersion = draftVersion.toBuilder()
                .purposeVersionState(PurposeVersionState.WAITING_FOR_APPROVAL)
                .build();
        Purpose waitingPurpose = draftPurpose.toBuilder().clearVersions().version(waitingVersion).build();
        when(currentUserSession.getActor()).thenReturn(User.getTenantAdmin(consumer));
        when(currentUserSession.getTenant()).thenReturn(consumer);
        when(currentChannel.getCurrentChannel()).thenReturn(Channel.M2M_V3);
        when(purposeUseCase.createPurpose(eService)).thenReturn(draftPurpose);
        when(purposeUseCase.askForApprovalPurpose(draftPurpose, draftVersion, null)).thenReturn(waitingPurpose);

        journey.createPurposeAndLinkToEService(eService, PurposeVersionState.REJECTED);

        InOrder order = inOrder(purposeUseCase, currentUserSession, currentChannel);
        order.verify(purposeUseCase).askForApprovalPurpose(draftPurpose, draftVersion, null);
        order.verify(currentChannel).setCurrentChannel(Channel.BFF);
        order.verify(currentUserSession).set(User.getTenantAdmin(producer), producer);
        order.verify(purposeUseCase).rejectPurpose(eq(waitingPurpose), eq(waitingVersion), anyString());
        order.verify(currentUserSession).set(User.getTenantAdmin(consumer), consumer);
        order.verify(currentChannel).setCurrentChannel(Channel.M2M_V3);
    }

    @Test
    void restoresTheConsumerSessionWhenTheRejectionFails() {
        PurposeVersion waitingVersion = draftVersion.toBuilder()
                .purposeVersionState(PurposeVersionState.WAITING_FOR_APPROVAL)
                .build();
        Purpose waitingPurpose = draftPurpose.toBuilder().clearVersions().version(waitingVersion).build();
        IllegalStateException failure = new IllegalStateException("boom");
        when(currentUserSession.getActor()).thenReturn(User.getTenantAdmin(consumer));
        when(currentUserSession.getTenant()).thenReturn(consumer);
        when(currentChannel.getCurrentChannel()).thenReturn(Channel.BFF);
        when(purposeUseCase.createPurpose(eService)).thenReturn(draftPurpose);
        when(purposeUseCase.askForApprovalPurpose(draftPurpose, draftVersion, null)).thenReturn(waitingPurpose);
        when(purposeUseCase.rejectPurpose(eq(waitingPurpose), eq(waitingVersion), anyString())).thenThrow(failure);

        assertThatThrownBy(() -> journey.createPurposeAndLinkToEService(eService, PurposeVersionState.REJECTED))
                .isSameAs(failure);

        verify(currentUserSession).set(User.getTenantAdmin(consumer), consumer);
        verify(currentChannel, times(2)).setCurrentChannel(Channel.BFF);
        assertThat(failure.getSuppressed()).isEmpty();
    }
}
