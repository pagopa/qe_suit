package it.pagopa.interop.web.shared;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.component.Alert;
import it.pagopa.interop.web.service.BrowserService;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebComponentAssertionController {
    private final BrowserService webBrowserService;

    @Then("il radio group {generalInformationRadioGroup} è disabilitato")
    public void assertRadioGroupDisabled(Boolean isDisabled) {
        assertThat(isDisabled)
                .as("Il radio group deve essere disabilitato")
                .isTrue();
    }

    @And("viene mostrato l'alert relativo al {generalInformationAlert} in stile warning {string}")
    public void assertAlert(Alert alert, String message) {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(alert.isWarning())
                    .as("L'alert è di tipo warning")
                    .isTrue();

            softly.assertThat(alert.message().read())
                    .as("Il messaggio dell'alert")
                    .isEqualTo(message);
        });
    }

    @Then("la checkbox {techSpecCheckbox} è disabilitata")
    public void checkboxIsDisabled(Boolean isDisabled) {
        assertThat(isDisabled)
                .as("La checkbox deve essere disabilitata")
                .isTrue();
    }

    @Given("si trova allo step {eServiceCreationStep}")
    public void assertComponentLoaded(Component currentStep) {
        currentStep.assertLoaded();
    }

    @Then("la creazione non prosegue ed il campo {techSpecErrorMessage} dello step Specifiche tecniche è evidenziato come errore mostrando il messaggio {string}")
    @When("la creazione non prosegue ed il campo {generalInformationErrorMessage} dello step Dati Generali è evidenziato come errore mostrando il messaggio {string}")
    public void assertFieldError(String actualErrorMessage, String expectedErrorMessage) {
        assertThat(actualErrorMessage)
                .as("Il messaggio di errore deve essere: " + expectedErrorMessage)
                .isEqualTo(expectedErrorMessage);
    }

    @Then("viene mostrata la snackbar con un messaggio di errore contenente {string}")
    public void assertSnackbar(String errorMessage) {
        String actualErrorMessage = webBrowserService.getSnackbarErrorMessage();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nella snackbar")
                .contains(errorMessage);
    }
}
