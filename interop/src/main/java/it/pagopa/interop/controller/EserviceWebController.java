package it.pagopa.interop.controller;

import io.cucumber.java.en.When;
import it.pagopa.interop.service.eservice.EServiceWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EserviceWebController {

    private final EServiceWebService eServiceWebService;

    @When("l'utente compila il form di creazione dell'eService con dati validi e invia la richiesta")
    public void createEservice() {
        eServiceWebService.publishEServiceWithDefault();
    }

    @When("l'utente invia la form dello step Dati Generali senza compilare i campi obbligatori")
    public void fillGeneralInformationWithoutRequired() {
        eServiceWebService.fillGeneralInformationAndSave((seed) ->{
            seed.eservice().name("");
            seed.eservice().description("");
        });
    }

    @When("il campo Nome dello step Dati Generali è evidenziato come errore e viene mostrato il messaggio di validazione {string}")
    public void assertNameTextFieldError(String errorMessage) {
        String actualErrorMessage = eServiceWebService.getNameTextFieldError();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nel campo Client Assertion")
                .isEqualTo(errorMessage);
    }

    @When("il campo Descrizione dello step Dati Generali è evidenziato come errore e viene mostrato il messaggio di validazione {string}")
    public void assertDescriptionTextFieldError(String errorMessage) {
        String actualErrorMessage = eServiceWebService.getDescriptionTextFieldError();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nel campo Client Assertion")
                .isEqualTo(errorMessage);
    }
}