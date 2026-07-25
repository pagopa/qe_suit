package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;

public interface PurposeJourney<SELF extends PurposeJourney<SELF>> extends JourneyModule {
    SELF linkPurpose(PurposeVersionState state);

    SELF linkPurpose(EService eService, PurposeVersionState state);
}
