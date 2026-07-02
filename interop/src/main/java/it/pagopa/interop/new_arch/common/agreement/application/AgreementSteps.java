package it.pagopa.interop.new_arch.common.agreement.application;

import io.cucumber.java.en.Given;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgreementSteps {
    private final AgreementUseCase agreementUseCase;

    @Given("associa un Agreement in stato DRAFT all'{eService}")
    public void createAgreement(EService eService) {
        agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor(), null);
    }
}
