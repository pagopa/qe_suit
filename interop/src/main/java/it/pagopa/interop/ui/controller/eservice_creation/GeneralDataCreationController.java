package it.pagopa.interop.ui.controller.eservice_creation;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.ui.domain.model.eservice_creation.GeneralDataSpecModel;
import it.pagopa.interop.ui.service.eservice_creation.GeneralDataService;
import it.pagopa.interop.ui.domain.component.Alert;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GeneralDataCreationController {

    private final GeneralDataService generalDataService;

    @When("compila lo step 'Informazioni generali' con i valori di default ma specificando:")
    public void fillGeneralInformationWithOverrides(EServiceSeed eserviceSeed) {
        generalDataService.fillWithOverrides(new GeneralDataSpecModel(eserviceSeed));
    }

    @When("compila lo step 'Informazioni generali' con i valori di default")
    public void fillGeneralInformation() {
        generalDataService.fill();
    }

    @When("la creazione non prosegue ed il campo {generalInformationErrorMessage} dello step Dati Generali è evidenziato come errore mostrando il messaggio {string}")
    public void assertFieldError(String actualErrorMessage, String expectedErrorMessage) {
        assertThat(actualErrorMessage)
                .as("Il messaggio di errore deve essere: " + expectedErrorMessage)
                .isEqualTo(expectedErrorMessage);
    }

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
}