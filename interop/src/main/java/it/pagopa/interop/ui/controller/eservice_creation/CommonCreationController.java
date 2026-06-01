package it.pagopa.interop.ui.controller.eservice_creation;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonCreationController {

    private final EServiceCreationPage eServiceCreationPage;

    @Given("si trova allo step {eServiceCreationStep}")
    public void assertStepLoaded(Component currentStep) {
        currentStep.assertLoaded();
    }

    @When("clicca sul button 'Salva bozza e prosegui'")
    public void saveDraft() {
        eServiceCreationPage.saveDraftButton().click();
    }

    @Then("la creazione non prosegue ed il campo {techSpecErrorMessage} dello step Specifiche tecniche è evidenziato come errore mostrando il messaggio {string}")
    @When("la creazione non prosegue ed il campo {generalInformationErrorMessage} dello step Dati Generali è evidenziato come errore mostrando il messaggio {string}")
    public void assertFieldError(String actualErrorMessage, String expectedErrorMessage) {
        assertThat(actualErrorMessage)
                .as("Il messaggio di errore deve essere: " + expectedErrorMessage)
                .isEqualTo(expectedErrorMessage);
    }
}