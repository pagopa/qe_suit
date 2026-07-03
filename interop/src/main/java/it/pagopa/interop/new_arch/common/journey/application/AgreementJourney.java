package it.pagopa.interop.new_arch.common.journey.application;

import it.pagopa.interop.new_arch.common.agreement.domain.AgreementState;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;

public interface AgreementJourney<SELF extends AgreementJourney<SELF>> {
    SELF linkAgreement(AgreementState agreementState, DelegationRef delegationRef);

    default SELF linkAgreement(AgreementState agreementState) {
        return linkAgreement(agreementState, null);
    }
}
