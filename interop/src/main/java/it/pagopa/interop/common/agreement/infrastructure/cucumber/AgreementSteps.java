package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgreementSteps {
    private final AgreementUseCase agreementUseCase;

    @Given("associa un Agreement in stato DRAFT all'{currentEService}")
    public void createAgreement(EService eService) {
        agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor());
    }
}
