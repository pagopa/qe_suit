package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;

import java.util.List;

/**
 * Creates reviewer fixtures on the configured channel and restores the caller's
 * initialized user/tenant session, including on failure.
 */
public interface RiskAnalysisSetupJourney<SELF extends RiskAnalysisSetupJourney<SELF>> extends JourneyModule {
    SELF setUpRiskAnalysisToCompile(String eServiceName, Tenant producer, Tenant consumer, User reviewer);

    SELF setUpRiskAnalysisToCompile(
            String eServiceName,
            Tenant producer,
            Tenant consumer,
            List<User> reviewers
    );

    SELF setUpRiskAnalysisToApprove(String eServiceName, Tenant producer, Tenant consumer, User reviewer);

    SELF setUpRiskAnalysisToApprove(
            String eServiceName,
            Tenant producer,
            Tenant consumer,
            List<User> reviewers
    );
}
