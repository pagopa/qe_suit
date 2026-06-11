package it.pagopa.interop.bff.controller;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.bff.model.ProducerKeychain;
import it.pagopa.interop.bff.service.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.service.producer_keychain.IProducerKeychainService;
import it.pagopa.interop.bff.service.producer_keychain.ProducerKeychainService;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerKeychainController {

    private final ProducerKeychainService service;

    @Given("una lista di {int} Producer Keychain")
    public void createProducerKeychains(int size) {
        for (int i = 0; i < size; i++)
            service
                .create()
                .withPolling(PollingStrategy.UNTIL_SUCCESS);
    }

    @When("l'utente legge tutti i Producer Keychain")
    public void getProducerKeychains() {
        Assertions.assertThat(readAllKeychains())
                .as("La lista dei Producer Keychain non deve essere null")
                .isNotNull();
    }

    @When("l'utente legge tutti gli {int} Producer Keychain")
    public void getProducerKeychains(Integer size) {
        Assertions.assertThat(readAllKeychains())
                .as("La lista dei Producer Keychain deve contenere " + size + " elementi")
                .hasSize(size);
    }

    @When("l'utente elimina tutti i Producer Keychain")
    public void deleteProducerKeychains() {
        var response = readAllKeychains();

        while (!response.isEmpty()) {

            // Elimina in blocco tutti i keychain trovati nella pagina corrente
            response.forEach(keychain ->
                    service.delete(keychain.getId())
                            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            );

            // Ricarica la lista per verificare se ci sono altre pagine o elementi residui
            response = readAllKeychains();
        }
    }

    @Then("tutti i Producer Keychain sono stati eliminati")
    public void verifyProducerKeychainsDeleted() {
        Assertions.assertThat(readAllKeychains())
                .as("La lista dei Producer Keychain deve essere vuota dopo l'eliminazione")
                .isEmpty();
    }

    private List<ProducerKeychain> readAllKeychains() {
        return service
                .readAll(buildGetAllRequest())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .getModels();
    }

    private IProducerKeychainService.GetAllRequest buildGetAllRequest() {
        return new IProducerKeychainService.GetAllRequest(0, 100, null, null, null);
    }
}
