package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.purpose.PurposeVersionState;

public interface PurposeJourney<SELF extends PurposeJourney<SELF>> {
    SELF addPurpose(PurposeVersionState state);
}
