package it.pagopa.interop.new_arch.common.journey.infrastructure;

import it.pagopa.interop.new_arch.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementState;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.ScenarioContext;
import it.pagopa.interop.new_arch.common.journey.application.AgreementJourney;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgreementJourneyImpl implements AgreementJourney<AgreementJourneyImpl> {
    private final AgreementUseCase agreementUseCase;
    private final ScenarioContext scenarioContext;

    @Override
    public AgreementJourneyImpl linkAgreement(AgreementState agreementState, DelegationRef delegationRef) {
        EService eService = scenarioContext.getLastOrThrow(EService.class);
        Agreement agreement = agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor(), delegationRef);

        switch(agreementState) {
            case DRAFT -> {}
            case PENDING -> agreementUseCase.submitAgreement(agreement);
            case ACTIVE -> agreementUseCase.activateAgreement(agreement, delegationRef);
            default -> throw new UnsupportedOperationException("Not implemented yet: " + agreementState);
        }

        return this;
    }
}
