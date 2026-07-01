package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.purpose.PurposeVersionState;

public interface IPurposeJourney<SELF extends IPurposeJourney<SELF>> {
    SELF createPurpose(PurposeVersionState state);
}
