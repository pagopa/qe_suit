package it.pagopa.interop.new_arch.common.journey.application;

import it.pagopa.interop.new_arch.common.agreement.domain.AgreementState;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import jakarta.annotation.Nullable;

public interface AgreementJourney<SELF extends AgreementJourney<SELF>> extends JourneyModule {

    SELF linkAgreementInState(EService eService, AgreementState agreementState, @Nullable DelegationRef delegationRef);

    default SELF linkAgreementInState(EService eService, AgreementState agreementState){
        return linkAgreementInState(eService, agreementState, null);
    }

    SELF linkAgreementInState(AgreementState agreementState, @Nullable DelegationRef delegationRef);

    default SELF linkAgreementInState(AgreementState agreementState) {
        return linkAgreementInState(agreementState, null);
    }
}
