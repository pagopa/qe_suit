package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;

import java.util.List;
import java.util.UUID;

public interface PurposeJourney<SELF extends PurposeJourney<SELF>> extends JourneyModule {
    SELF createPurpose(PurposeVersionState state);

    SELF createPurposeAndLinkToEService(EService eService, PurposeVersionState state);

    SELF createReversePurposeAndLinkToEService(EService eService, EServiceRiskAnalysis riskAnalysis, PurposeVersionState state);

    SELF createPurposeFromTemplateAndLinkToEService(PurposeTemplate purposeTemplate, EService eService, PurposeVersionState state);

    SELF assignRiskAnalysisReviewers(RiskAnalysisReviewMode reviewMode, List<UUID> reviewerIds);

    SELF submitRiskAnalysis();

    SELF signRiskAnalysis(int metadataVersionToSign);

    SELF rejectRiskAnalysis(String rejectionReason);
}
