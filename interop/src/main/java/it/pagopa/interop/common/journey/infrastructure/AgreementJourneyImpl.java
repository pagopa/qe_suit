package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.AgreementJourney;
import it.pagopa.interop.common.kernel.domain.Delegation;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgreementJourneyImpl implements AgreementJourney<AgreementJourneyImpl> {
    private final AgreementUseCase agreementUseCase;
    private final EntityStore entityStore;

    @Override
    public AgreementJourneyImpl linkAgreement(EService eService, AgreementState agreementState, @Nullable Delegation delegation) {
        agreementUseCase.createAgreement(eService, eService.getActiveDescriptor(), agreementState, delegation);
        return this;
    }

    @Override
    public AgreementJourneyImpl linkAgreement(AgreementState agreementState, @Nullable Delegation delegation) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        return linkAgreement(eService, agreementState, delegation);
    }
}
