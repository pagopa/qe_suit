package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.AgreementJourney;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgreementJourneyImpl implements AgreementJourney<AgreementJourneyImpl> {
    private final AgreementUseCase agreementUseCase;
    private final EntityStore entityStore;

    @Override
    public AgreementJourneyImpl linkAgreement(EService eService, AgreementState agreementState, @org.jspecify.annotations.Nullable Delegation delegation) {
        Agreement agreement = agreementUseCase.createAgreement(eService, eService.getActiveDescriptor(), delegation);

        switch (agreementState) {
            case DRAFT -> {
            }
            case ACTIVE -> {
                Agreement submitted = agreementUseCase.submitAgreement(agreement);
                PollingUtils.pollUntil(
                        () -> agreementUseCase.getAgreement(submitted),
                        refreshed -> refreshed.getState() == AgreementState.ACTIVE,
                        "La richiesta di fruizione non ha raggiunto lo stato ACTIVE"
                );
            }
            case PENDING -> agreementUseCase.submitAgreement(agreement);
            default -> throw new UnsupportedOperationException("Not implemented yet: " + agreementState);
        }

        return this;
    }

    @Override
    public AgreementJourneyImpl linkAgreement(AgreementState agreementState, @Nullable Delegation delegation) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        return linkAgreement(eService, agreementState, delegation);
    }
}
