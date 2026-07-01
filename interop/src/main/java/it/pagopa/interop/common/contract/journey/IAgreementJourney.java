package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.agreement.AgreementState;

public interface IAgreementJourney<SELF extends IAgreementJourney<SELF>> {
    SELF addAgreement(AgreementState agreementState);
}
