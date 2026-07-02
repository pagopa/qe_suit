package it.pagopa.interop.new_arch.common.journey.application;

import it.pagopa.interop.new_arch.common.agreement.domain.AgreementState;

public interface AgreementJourney<SELF extends AgreementJourney<SELF>> {
    SELF linkAgreement(AgreementState agreementState);
}
