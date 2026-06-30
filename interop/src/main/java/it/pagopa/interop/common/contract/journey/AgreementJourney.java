package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.agreement.AgreementState;

public interface AgreementJourney<SELF extends AgreementJourney<SELF>> {
    SELF addAgreement(AgreementState agreementState);
}
