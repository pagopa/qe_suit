package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.journey.application.RiskAnalysisSetupJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RiskAnalysisSetupJourneyImpl implements RiskAnalysisSetupJourney<RiskAnalysisSetupJourneyImpl> {
    private final EServiceJourneyImpl eServiceJourney;
    private final AgreementJourneyImpl agreementJourney;
    private final PurposeJourneyImpl purposeJourney;
    private final CurrentUserSession session;

    @Override
    public RiskAnalysisSetupJourneyImpl setUpRiskAnalysisToCompile(
            String eServiceName, Tenant producer, Tenant consumer, User reviewer
    ) {
        return setUp(eServiceName, producer, consumer, reviewer,
                RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS);
    }

    @Override
    public RiskAnalysisSetupJourneyImpl setUpRiskAnalysisToApprove(
            String eServiceName, Tenant producer, Tenant consumer, User reviewer
    ) {
        return setUp(eServiceName, producer, consumer, reviewer,
                RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS);
    }

    private RiskAnalysisSetupJourneyImpl setUp(
            String eServiceName, Tenant producer, Tenant consumer, User reviewer, RiskAnalysisReviewMode mode
    ) {
        Objects.requireNonNull(eServiceName, "eServiceName must not be null");
        if (eServiceName.isBlank()) {
            throw new IllegalArgumentException("eServiceName must not be blank");
        }
        Objects.requireNonNull(producer, "producer must not be null");
        Objects.requireNonNull(consumer, "consumer must not be null");
        Objects.requireNonNull(reviewer, "reviewer must not be null");
        Objects.requireNonNull(reviewer.getUserId(), "reviewer userId must not be null");

        User originalUser = session.getUser();
        Tenant originalTenant = session.getTenant();
        Throwable failure = null;
        try {
            session.set(User.getTenantAdmin(producer), producer);
            eServiceJourney.createEService(command -> command.name(eServiceName), EServiceDescriptorState.PUBLISHED);
            session.set(User.getTenantAdmin(consumer), consumer);
            agreementJourney.linkAgreement(AgreementState.ACTIVE);
            purposeJourney.createPurpose(PurposeVersionState.DRAFT);
            purposeJourney.assignRiskAnalysisReviewers(mode, List.of(reviewer.getUserId()));
            if (mode == RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS) {
                purposeJourney.submitRiskAnalysis();
            }
            return this;
        } catch (RuntimeException | Error ex) {
            failure = ex;
            throw ex;
        } finally {
            try {
                session.set(originalUser, originalTenant);
            } catch (RuntimeException | Error restoreFailure) {
                if (failure == null) {
                    throw restoreFailure;
                }
                failure.addSuppressed(restoreFailure);
            }
        }
    }
}
