package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.common.journey.application.AgreementJourney;
import it.pagopa.interop.common.kernel.domain.Delegation;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgreementJourneyImpl implements AgreementJourney<AgreementJourneyImpl> {
    private final AgreementUseCase agreementUseCase;
    private final DomainContext domainContext;

    @Override
    public AgreementJourneyImpl linkAgreement(EService eService, AgreementState agreementState, @org.jspecify.annotations.Nullable Delegation delegation) {
        Agreement agreement = agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor(), delegation);

        switch (agreementState) {
            case DRAFT -> {
            }
            case PENDING -> agreementUseCase.submitAgreement(agreement);
            case ACTIVE -> agreementUseCase.activateAgreement(agreement, delegation);
            default -> throw new UnsupportedOperationException("Not implemented yet: " + agreementState);
        }

        return this;
    }

    @Override
    public AgreementJourneyImpl linkAgreement(AgreementState agreementState, @Nullable Delegation delegation) {
        EService eService = domainContext.getLastOrThrow(EService.class);
        return linkAgreement(eService, agreementState, delegation);
    }
}
