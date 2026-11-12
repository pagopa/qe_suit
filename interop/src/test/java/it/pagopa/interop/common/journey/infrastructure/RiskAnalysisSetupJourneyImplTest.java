package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.journey.infrastructure.config.JourneyProxyConfig;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskAnalysisSetupJourneyImplTest {
    @Captor
    private ArgumentCaptor<Consumer<EServiceCreationCommand>> commandConfig;
    private final EServiceJourneyImpl services = mock(EServiceJourneyImpl.class);
    private final AgreementJourneyImpl agreements = mock(AgreementJourneyImpl.class);
    private final PurposeJourneyImpl purposes = mock(PurposeJourneyImpl.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;
    private final Tenant consumer = Tenant.SOGECAP;
    private final User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);
    private final User originalUser = User.getTenantAdmin(Tenant.PAGO_PA);
    private final RiskAnalysisSetupJourneyImpl setup =
            new RiskAnalysisSetupJourneyImpl(services, agreements, purposes, session);

    @BeforeEach
    void originalSession() {
        lenient().when(session.getUser()).thenReturn(originalUser);
        lenient().when(session.getTenant()).thenReturn(Tenant.PAGO_PA);
    }

    @ParameterizedTest
    @EnumSource(value = RiskAnalysisReviewMode.class,
            names = {"REVIEWER_WRITES_REVIEWER_SIGNS", "ADMIN_WRITES_REVIEWER_SIGNS"})
    void createsPrerequisitesWithCorrectActorsAndMode(RiskAnalysisReviewMode mode) {
        assertThat(runSetup(mode)).isSameAs(setup);

        var order = inOrder(session, services, agreements, purposes);
        order.verify(session).set(User.getTenantAdmin(producer), producer);
        order.verify(services).createEService(commandConfig.capture(), eq(EServiceDescriptorState.PUBLISHED));
        order.verify(session).set(User.getTenantAdmin(consumer), consumer);
        order.verify(agreements).linkAgreement(AgreementState.ACTIVE);
        order.verify(purposes).createPurpose(PurposeVersionState.DRAFT);
        order.verify(purposes).assignRiskAnalysisReviewers(mode, List.of(reviewer.getUserId()));
        if (mode == RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS) {
            order.verify(purposes).submitRiskAnalysis();
        } else {
            verify(purposes, never()).submitRiskAnalysis();
        }
        order.verify(session).set(originalUser, Tenant.PAGO_PA);
        verifyNoMoreInteractions(services, agreements, purposes);
        EServiceCreationCommand command = mock(EServiceCreationCommand.class);
        commandConfig.getValue().accept(command);
        verify(command).name("risk-analysis-fixture");
    }

    @Test
    void stopsAndRestoresSessionWhenAssignmentFails() {
        var failure = new IllegalStateException("Assignment failed");
        doThrow(failure).when(purposes).assignRiskAnalysisReviewers(any(), anyList());

        assertThatThrownBy(() -> setup.setUpRiskAnalysisToApprove(
                "fixture", producer, consumer, reviewer)).isSameAs(failure);

        verify(purposes, never()).submitRiskAnalysis();
        verify(session).set(originalUser, Tenant.PAGO_PA);
    }

    @Test
    void preservesPrimaryFailureWhenRestorationFails() {
        var failure = new IllegalStateException("Assignment failed");
        var restorationFailure = new IllegalStateException("Restoration failed");
        doThrow(failure).when(purposes).assignRiskAnalysisReviewers(any(), anyList());
        doThrow(restorationFailure).when(session).set(originalUser, Tenant.PAGO_PA);

        assertThatThrownBy(() -> setup.setUpRiskAnalysisToApprove("fixture", producer, consumer, reviewer))
                .isSameAs(failure).hasSuppressedException(restorationFailure);
    }

    @Test
    void reportsRestorationFailureAfterSuccessfulSetup() {
        var failure = new IllegalStateException("Restoration failed");
        doThrow(failure).when(session).set(originalUser, Tenant.PAGO_PA);

        assertThatThrownBy(() -> setup.setUpRiskAnalysisToCompile("fixture", producer, consumer, reviewer))
                .isSameAs(failure);
    }

    @Test
    void requiresAnInitializedCallerSessionBeforeCreatingResources() {
        when(session.getUser()).thenThrow(new IllegalStateException("Current user is not set"));

        assertThatThrownBy(() -> setup.setUpRiskAnalysisToCompile("fixture", producer, consumer, reviewer))
                .isInstanceOf(IllegalStateException.class).hasMessage("Current user is not set");
        verifyNoInteractions(services, agreements, purposes);
        verify(session, never()).set(any(), any());
    }

    @Test
    void rejectsMissingInputsBeforeCreatingResources() {
        assertThatThrownBy(() -> setup.setUpRiskAnalysisToCompile(null, producer, consumer, reviewer))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> setup.setUpRiskAnalysisToApprove(" ", producer, consumer, reviewer))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> setup.setUpRiskAnalysisToCompile("fixture", null, consumer, reviewer))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> setup.setUpRiskAnalysisToApprove("fixture", producer, null, reviewer))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> setup.setUpRiskAnalysisToCompile("fixture", producer, consumer, null))
                .isInstanceOf(NullPointerException.class);
        verifyNoInteractions(services, agreements, purposes);
        verify(session, never()).set(any(), any());
    }

    @Test
    void keepsNewAndExistingOperationsFluentThroughAggregateProxy() {
        EntityStore store = mock(EntityStore.class);
        Purpose purpose = Purpose.builder().id(UUID.randomUUID()).build();
        when(store.getLastOrThrow(Purpose.class)).thenReturn(purpose);
        InteropJourney journey = new JourneyProxyConfig().interopJourney(
                List.of(setup, new UserJourneyImpl(session), new FinalizerJourneyImpl(store)));

        assertThat(journey.withConsumer(consumer, reviewer)).isSameAs(journey);
        assertThat(journey.setUpRiskAnalysisToCompile("fixture", producer, consumer, reviewer)).isSameAs(journey);
        assertThat(journey.get(Purpose.class)).isSameAs(purpose);
        assertThat(journey.withProducer(producer, UserRole.ADMIN)).isSameAs(journey);
        verify(session).set(reviewer, consumer);
        verify(store).getLastOrThrow(Purpose.class);
    }

    private RiskAnalysisSetupJourneyImpl runSetup(RiskAnalysisReviewMode mode) {
        return mode == RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS
                ? setup.setUpRiskAnalysisToCompile("risk-analysis-fixture", producer, consumer, reviewer)
                : setup.setUpRiskAnalysisToApprove("risk-analysis-fixture", producer, consumer, reviewer);
    }
}
