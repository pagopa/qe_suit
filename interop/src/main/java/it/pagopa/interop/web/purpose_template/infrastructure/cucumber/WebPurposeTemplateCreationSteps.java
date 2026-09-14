package it.pagopa.interop.web.purpose_template.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebPurposeTemplateCreationSteps {

    private final PurposeTemplateUseCase purposeTemplateUseCase;

    @When("{tenant} tenta di creare un template di finalità")
    public void createPurposeTemplate(Tenant tenant) {
        purposeTemplateUseCase.createPurposeTemplate(tenant);
    }

    @Then("{tenant} visualizza la corretta pagina di Informazioni Generali")
    public void assertGeneralInformationPageDisplayed(Tenant tenant) {
        purposeTemplateUseCase.assertGeneralInformationPageDisplayed(tenant);
    }

}

