package it.pagopa.interop.ui.controller.eservice_creation;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.ui.domain.model.eservice_creation.TechnicalSpecModel;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent;
import it.pagopa.interop.ui.service.eservice_creation.TechnicalDataService;

import static org.assertj.core.api.Assertions.assertThat;

public class TechnicalDataCreationController {

    private final TechnicalSpecificationStepComponent technicalSpecificationStep;
    private final TechnicalDataService technicalDataService;

    public TechnicalDataCreationController(EServiceCreationPage eServiceCreationPage, TechnicalDataService technicalDataService) {
        technicalSpecificationStep = eServiceCreationPage.technicalSpecificationStep();
        this.technicalDataService = technicalDataService;
    }

    @When("cancella i valori da tutti gli input delle specifiche tecniche")
    public void cleanInput() {
        technicalDataService.fill(TechnicalSpecModel.buildEmpty());
    }

    @Then("la creazione non prosegue ed il campo {techSpecErrorMessage} dello step Specifiche tecniche è evidenziato come errore mostrando il messaggio {string}")
    public void assertFieldError(String actualErrorMessage, String expectedErrorMessage) {
        assertThat(actualErrorMessage)
                .as("Il messaggio di errore deve essere: " + expectedErrorMessage)
                .isEqualTo(expectedErrorMessage);
    }

    @Then("la checkbox {techSpecCheckbox} è disabilitata")
    public void checkboxIsDisabled(Boolean isDisabled) {
        assertThat(isDisabled)
                .as("La checkbox deve essere disabilitata")
                .isTrue();
    }
}
