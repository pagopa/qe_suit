package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgreementSteps {
    private final AgreementUseCase agreementUseCase;

    @Given("associa un Agreement in stato DRAFT all'{currentEService}")
    public void createAgreement(EService eService) {
        agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor());
    }

    @When("il sistema impedisce a/al {tenant} di inoltrare una richiesta di fruizione per la {currentDeprecatedEServiceDescriptor} dell'{currentEService}")
    public void createAgreement(EServiceDescriptor eServiceDescriptor, EService eService) {
        agreementUseCase.shouldFailToCreateAgreement(eService, eServiceDescriptor, AgreementCreationFailureReason.DEPRECATED_VERSION);
    }

}
