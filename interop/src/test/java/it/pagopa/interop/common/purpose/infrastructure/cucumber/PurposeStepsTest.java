package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PurposeStepsTest {
    private final EntityStore entities = mock(EntityStore.class);
    private final InteropJourney interopJourney = mock(InteropJourney.class);
    private final PurposeUseCase useCase = mock(PurposeUseCase.class);
    private final AuthSession userSession = mock(AuthSession.class);
    private final CurrentChannel<Channel> currentChannel = mock(CurrentChannel.class);
    private final PurposeSteps steps = new PurposeSteps(entities, interopJourney, useCase, userSession, currentChannel);
    private final PurposeVersion waiting = PurposeVersion.builder()
            .id(UUID.randomUUID()).createdAt(Instant.EPOCH)
            .purposeVersionState(PurposeVersionState.WAITING_FOR_APPROVAL).build();
    private final Purpose purpose = Purpose.builder().id(UUID.randomUUID()).version(waiting).build();

    @ParameterizedTest
    @EnumSource(PurposeVersionState.class)
    void createsReversePurposeAsTheRequestedConsumer(PurposeVersionState state) {
        EService eService = EService.builder().id(UUID.randomUUID()).build();
        EServiceRiskAnalysis riskAnalysis = EServiceRiskAnalysis.builder().id(UUID.randomUUID()).build();
        when(entities.getLastOrThrow(EService.class)).thenReturn(eService);
        when(entities.getLastOrThrow(EServiceRiskAnalysis.class)).thenReturn(riskAnalysis);
        when(interopJourney.withConsumer(Tenant.COMUNE_DI_COMUN_NUOVO, UserRole.ADMIN))
                .thenReturn(interopJourney);

        steps.createReversePurposeForEService(state, Tenant.COMUNE_DI_COMUN_NUOVO);

        var order = inOrder(interopJourney);
        order.verify(interopJourney).withConsumer(Tenant.COMUNE_DI_COMUN_NUOVO, UserRole.ADMIN);
        order.verify(interopJourney).createReversePurposeAndLinkToEService(eService, riskAnalysis, state);
        verifyNoMoreInteractions(interopJourney);
        verifyNoInteractions(useCase, userSession, currentChannel);
    }

    @Test
    void failsBeforeCreatingReversePurposeWhenRiskAnalysisIsMissing() {
        EService eService = EService.builder().id(UUID.randomUUID()).build();
        when(entities.getLastOrThrow(EService.class)).thenReturn(eService);
        when(entities.getLastOrThrow(EServiceRiskAnalysis.class))
                .thenThrow(new IllegalStateException("Risk analysis not found"));

        assertThatThrownBy(() -> steps.createReversePurposeForEService(
                PurposeVersionState.ACTIVE, Tenant.COMUNE_DI_COMUN_NUOVO))
                .isInstanceOf(IllegalStateException.class).hasMessage("Risk analysis not found");

        verifyNoInteractions(interopJourney, useCase, userSession, currentChannel);
    }

    @ParameterizedTest
    @EnumSource(value = Channel.class, names = {"BFF", "M2M_V3"})
    void updatesDailyCallsAsThePurposeConsumerUsingItsOwnEService(Channel channel) {
        when(currentChannel.getCurrentChannel()).thenReturn(channel);
        var eService = EService.builder().id(UUID.randomUUID()).build();
        var activePurpose = result(PurposeVersionState.ACTIVE).toBuilder()
                .eserviceId(eService.getId()).consumerId(Tenant.COMUNE_DI_COMUN_NUOVO.getOrganizationId()).build();
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(activePurpose);
        when(entities.getById(eService.getId(), EService.class)).thenReturn(Optional.of(eService));

        steps.updatePurposeDailyCallsEstimateBeyondThreshold();

        verifySession(Tenant.COMUNE_DI_COMUN_NUOVO);
        verifyNoInteractions(currentChannel);
        verify(useCase).updatePurposeDailyCallsEstimateBeyondThreshold(activePurpose, eService);
    }

    @Test
    void failsBeforeUpdatingWhenThePurposeEServiceIsMissing() {
        var activePurpose = result(PurposeVersionState.ACTIVE).toBuilder().eserviceId(UUID.randomUUID()).build();
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(activePurpose);

        assertThatThrownBy(steps::updatePurposeDailyCallsEstimateBeyondThreshold)
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("E-service not found");
        verifyNoInteractions(useCase, userSession, currentChannel);
    }

    @ParameterizedTest
    @EnumSource(value = Channel.class, names = {"BFF", "M2M_V3"})
    void approvesWaitingVersionAsRequestedTenant(Channel channel) {
        when(currentChannel.getCurrentChannel()).thenReturn(channel);
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(purpose);
        when(useCase.activatePurpose(purpose, waiting, null)).thenReturn(result(PurposeVersionState.ACTIVE));

        steps.approvePurpose(Tenant.COMUNE_DI_MILANO, PurposeVersionState.ACTIVE);

        verifySession(Tenant.COMUNE_DI_MILANO);
        verifyNoInteractions(currentChannel);
        verify(useCase).activatePurpose(purpose, waiting, null);
    }

    @ParameterizedTest
    @EnumSource(value = Channel.class, names = {"BFF", "M2M_V3"})
    void rejectsWaitingVersionWithAgreedReason(Channel channel) {
        when(currentChannel.getCurrentChannel()).thenReturn(channel);
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(purpose);
        when(useCase.rejectPurpose(purpose, waiting, "Rifiutata nell'ambito del test"))
                .thenReturn(result(PurposeVersionState.REJECTED));

        steps.rejectPurpose(Tenant.COMUNE_DI_MILANO, PurposeVersionState.REJECTED);

        verifySession(Tenant.COMUNE_DI_MILANO);
        verifyNoInteractions(currentChannel);
        verify(useCase).rejectPurpose(purpose, waiting, "Rifiutata nell'ambito del test");
    }

    @ParameterizedTest
    @EnumSource(value = Channel.class, names = {"BFF", "M2M_V3"})
    void failsBeforeOperationWhenNoWaitingVersionExists(Channel channel) {
        when(currentChannel.getCurrentChannel()).thenReturn(channel);
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(result(PurposeVersionState.ACTIVE));

        assertThatThrownBy(() -> steps.rejectPurpose(Tenant.COMUNE_DI_MILANO, PurposeVersionState.REJECTED))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("waiting for approval");
        verifyNoInteractions(useCase);
    }

    @ParameterizedTest
    @EnumSource(value = Channel.class, names = {"BFF", "M2M_V3"})
    void checksRequestedStateOfTheProcessedVersion(Channel channel) {
        when(currentChannel.getCurrentChannel()).thenReturn(channel);
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(purpose);
        when(useCase.rejectPurpose(purpose, waiting, "Rifiutata nell'ambito del test"))
                .thenReturn(result(PurposeVersionState.ACTIVE));

        assertThatThrownBy(() -> steps.rejectPurpose(Tenant.COMUNE_DI_MILANO, PurposeVersionState.REJECTED))
                .isInstanceOf(AssertionError.class);
    }

    private void verifySession(Tenant tenant) {
        verify(userSession).set(tenant);
        verifyNoMoreInteractions(userSession);
    }

    private Purpose result(PurposeVersionState state) {
        return purpose.toBuilder().clearVersions()
                .version(waiting.toBuilder().purposeVersionState(state).build()).build();
    }
}
