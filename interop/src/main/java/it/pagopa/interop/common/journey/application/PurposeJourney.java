package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;

public interface PurposeJourney<SELF extends PurposeJourney<SELF>> extends JourneyModule {
    SELF createPurpose(PurposeVersionState state);

    SELF createPurposeAndLinkToEService(EService eService, PurposeVersionState state);

    SELF createReversePurposeAndLinkToEService(EService eService, java.util.UUID riskAnalysisId, PurposeVersionState state);

    SELF createPurposeFromTemplateAndLinkToEService(it.pagopa.interop.common.purpose_template.domain.PurposeTemplate purposeTemplate, EService eService, PurposeVersionState state);
}
