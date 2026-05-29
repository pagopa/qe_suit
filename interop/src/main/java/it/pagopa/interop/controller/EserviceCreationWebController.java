package it.pagopa.interop.controller;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.service.eservice.EServiceWebService;
import it.pagopa.interop.web.component.Alert;
import it.pagopa.interop.web.pages.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EserviceCreationWebController {

    private final EServiceWebService eServiceWebService;
    private final EServiceCreationPage eServiceCreationPage;

    @When("l'utente compila il form di creazione dell'eService con dati validi e invia la richiesta")
    public void createEservice() {
        eServiceWebService.publishEServiceWithDefault();
    }

    @When("l'utente compila lo step 'Informazioni generali' con i valori di default ma specificando:")
    public void fillGeneralInformationWithOverrides(EServiceSeed eserviceSeed) {
        eServiceWebService.fillGeneralInformationAndSave(targetStep -> {
            org.springframework.beans.BeanUtils.copyProperties(eserviceSeed, targetStep.eservice());
        });
    }

    @When("l'utente clicca sul button 'Salva bozza e prosegui'")
    public void saveDraft() {
        eServiceWebService.saveDraft();
    }

    @When("la creazione non prosegue ed il campo Nome dello step Dati Generali è evidenziato come errore mostrando il messaggio {string}")
    public void assertNameTextFieldError(String errorMessage) {
        String actualErrorMessage = eServiceWebService.getNameTextFieldError();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nel campo Client Assertion")
                .isEqualTo(errorMessage);
    }

    @When("la creazione non prosegue ed il campo Descrizione dello step Dati Generali è evidenziato come errore mostrando il messaggio {string}")
    public void assertDescriptionTextFieldError(String errorMessage) {
        String actualErrorMessage = eServiceWebService.getDescriptionTextFieldError();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nel campo Client Assertion")
                .isEqualTo(errorMessage);
    }

    @When("la creazione non prosegue ed il radio group 'L’e-service eroga dati personali?' è evidenziato come errore mostrando il messaggio {string}")
    public void assertPersonalDataRadioGroupError(String errorMessage) {
        String actualErrorMessage = eServiceCreationPage.generalInformationStep().personalData().getErrorMessage();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nel campo Client Assertion")
                .isEqualTo(errorMessage);
    }

    @Then("il radio group 'L'e-service eroga o riceve dati?' è disabilitato")
    public void asserModeRadioGroup() {
        boolean isDisabled = eServiceWebService.isModeRadioGroupDisabled();

        assertThat(isDisabled)
                .as("Stato del radio group 'L'e-service eroga o riceve dati?'")
                .isTrue();
    }

    @And("viene mostrato l'alert relativo al {string} in stile warning {string}")
    public void assertAlert(String type, String message) {
        Alert alert = switch (type) {
            case "keychain" -> eServiceWebService.getAyncKeychainWarningAlert();
            case "SOAP","soap" -> eServiceWebService.getAsyncSoapWarningAlert();
            default -> throw new IllegalArgumentException("Tipo di alert non riconosciuto: " + type);
        };

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