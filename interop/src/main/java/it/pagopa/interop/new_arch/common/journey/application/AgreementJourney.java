package it.pagopa.interop.new_arch.common.journey.application;

import it.pagopa.interop.new_arch.common.agreement.domain.AgreementState;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.kernel.domain.Delegation;
import jakarta.annotation.Nullable;

public interface AgreementJourney<SELF extends AgreementJourney<SELF>> extends JourneyModule {

    SELF linkAgreement(EService eService, AgreementState agreementState, @Nullable Delegation delegation);

    default SELF linkAgreement(EService eService, AgreementState agreementState){
        return linkAgreement(eService, agreementState, null);
    }

    SELF linkAgreement(AgreementState agreementState, @Nullable Delegation delegation);

    default SELF linkAgreement(AgreementState agreementState) {
        return linkAgreement(agreementState, null);
    }
}
