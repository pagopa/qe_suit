package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.application.EServiceRiskAnalysisUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisSigningState;
import it.pagopa.interop.common.purpose.infrastructure.cucumber.dto.PurposeAliasConfiguration;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.List;
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
    private final EServiceRiskAnalysisUseCase riskAnalysisUseCase = mock(EServiceRiskAnalysisUseCase.class);
    private final PurposeSteps steps = new PurposeSteps(entities, interopJourney, useCase, riskAnalysisUseCase, userSession);
    private final PurposeVersion waiting = PurposeVersion.builder()
            .id(UUID.randomUUID()).createdAt(Instant.EPOCH)
            .purposeVersionState(PurposeVersionState.WAITING_FOR_APPROVAL).build();
    private final Purpose purpose = Purpose.builder().id(UUID.randomUUID()).version(waiting).build();

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

    @Test
    void createsDraftPurposesForDeliverEServiceAndRegistersAliases() {
        var eService = EService.builder().id(UUID.randomUUID()).mode(EServiceMode.DELIVER).build();
        when(entities.getByAliasOrThrow("ES-1", EService.class)).thenReturn(eService);
        when(entities.getByAlias(anyString(), eq(Purpose.class))).thenReturn(Optional.empty());
        when(interopJourney.withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)).thenReturn(interopJourney);

        steps.draftPurposesCreatedForEServiceWithDistinctNames(
                Tenant.PAGO_PA,
                "ES-1",
                List.of(aliasConfig("F-1"), aliasConfig("F-2"))
        );

        verify(interopJourney, times(2)).withConsumer(Tenant.PAGO_PA, UserRole.ADMIN);
        verify(interopJourney).createPurposeAndLinkToEService(eService, PurposeVersionState.DRAFT, "F-1");
        verify(interopJourney).createPurposeAndLinkToEService(eService, PurposeVersionState.DRAFT, "F-2");
    }

    @Test
    void createsDraftReversePurposesForReceiveEServiceUsingProducerRiskAnalysis() {
        var eService = EService.builder().id(UUID.randomUUID()).mode(EServiceMode.RECEIVE).build();
        var riskAnalysis = EServiceRiskAnalysis.builder().id(UUID.randomUUID()).build();
        when(entities.getByAliasOrThrow("ES-1", EService.class)).thenReturn(eService);
        when(entities.getByAlias("F-1", Purpose.class)).thenReturn(Optional.empty());
        when(entities.getLast(EServiceRiskAnalysis.class)).thenReturn(Optional.empty());
        when(riskAnalysisUseCase.getRiskAnalysis(eService)).thenReturn(riskAnalysis);
        when(interopJourney.withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)).thenReturn(interopJourney);

        steps.draftPurposesCreatedForEServiceWithDistinctNames(
                Tenant.PAGO_PA,
                "ES-1",
                List.of(aliasConfig("F-1"))
        );

        verify(interopJourney).withConsumer(Tenant.PAGO_PA, UserRole.ADMIN);
        verify(interopJourney).createReversePurposeAndLinkToEService(
                eService,
                riskAnalysis,
                PurposeVersionState.DRAFT,
                "F-1"
        );
    }

    @Test
    void clonesSourcePurposeForSameEService() {
        var eService = EService.builder().id(UUID.randomUUID()).build();
        when(entities.getByAliasOrThrow("ES-1", EService.class)).thenReturn(eService);
        when(entities.getByAliasOrThrow("F-1", Purpose.class)).thenReturn(purpose);
        when(interopJourney.withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)).thenReturn(interopJourney);

        steps.anotherPurposeWithDuplicatedName(Tenant.PAGO_PA, "ES-1", "F-1");

        verify(interopJourney).withConsumer(Tenant.PAGO_PA, UserRole.ADMIN);
        verify(interopJourney).clonePurpose(purpose, eService);
    }

    @Test
    void createsDraftPurposeFromTemplateAndRegistersAlias() {
        var eService = EService.builder().id(UUID.randomUUID()).build();
        var template = PurposeTemplate.builder().id(UUID.randomUUID()).build();
        when(entities.getByAlias("F-2", Purpose.class)).thenReturn(Optional.empty());
        when(entities.getByAliasOrThrow("ES-1", EService.class)).thenReturn(eService);
        when(entities.getLastOrThrow(PurposeTemplate.class)).thenReturn(template);
        when(interopJourney.withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)).thenReturn(interopJourney);

        steps.draftPurposeCreatedFromTemplateForEService(Tenant.PAGO_PA, "ES-1", "F-2");

        verify(interopJourney).withConsumer(Tenant.PAGO_PA, UserRole.ADMIN);
        verify(interopJourney).createPurposeFromTemplateAndLinkToEService(
                template,
                eService,
                PurposeVersionState.DRAFT,
                "F-2"
        );
    }

    @Test
    void setsUpPurposeAndBringsRiskAnalysisToRequestedSigningStateForReviewer() {
        when(interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)).thenReturn(interopJourney);
        when(interopJourney.createEService(EServiceDescriptorState.PUBLISHED)).thenReturn(interopJourney);
        when(interopJourney.withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)).thenReturn(interopJourney);
        when(interopJourney.linkAgreement(AgreementState.ACTIVE)).thenReturn(interopJourney);
        when(interopJourney.createPurpose(PurposeVersionState.DRAFT)).thenReturn(interopJourney);
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(purpose);

        steps.purposeRiskAnalysisInStateAssignedToReviewer("approvata", Tenant.PAGO_PA);

        verify(interopJourney).bringPurposeRiskAnalysisToStateForReviewer(
                purpose,
                Tenant.PAGO_PA,
                RiskAnalysisSigningState.SIGNED
        );
    }

    private static PurposeAliasConfiguration aliasConfig(String alias) {
        var config = new PurposeAliasConfiguration();
        config.setAlias(alias);
        return config;
    }
}
