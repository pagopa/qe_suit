package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisSigningState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;

public interface PurposeJourney<SELF extends PurposeJourney<SELF>> extends JourneyModule {
    SELF createPurpose(PurposeVersionState state);

    SELF createPurposeAndLinkToEService(EService eService, PurposeVersionState state);

    SELF createPurposeAndLinkToEService(EService eService, PurposeVersionState state, String alias);

    SELF createReversePurposeAndLinkToEService(EService eService, EServiceRiskAnalysis riskAnalysis, PurposeVersionState state);

    SELF createReversePurposeAndLinkToEService(EService eService, EServiceRiskAnalysis riskAnalysis, PurposeVersionState state, String alias);

    SELF createPurposeFromTemplateAndLinkToEService(PurposeTemplate purposeTemplate, EService eService, PurposeVersionState state);

    SELF createPurposeFromTemplateAndLinkToEService(PurposeTemplate purposeTemplate, EService eService, PurposeVersionState state, String alias);

    SELF clonePurpose(Purpose purpose, EService eService);

    SELF bringPurposeRiskAnalysisToStateForReviewer(Purpose purpose, Tenant consumer, RiskAnalysisSigningState targetState);
}
