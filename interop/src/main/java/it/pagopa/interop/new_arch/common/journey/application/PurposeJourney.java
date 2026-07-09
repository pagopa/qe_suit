package it.pagopa.interop.new_arch.common.journey.application;

import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.purpose.domain.PurposeVersionState;

public interface PurposeJourney<SELF extends PurposeJourney<SELF>> extends JourneyModule {
    SELF linkPurpose(PurposeVersionState state);

    SELF linkPurpose(EService eService, PurposeVersionState state);
}
