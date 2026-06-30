package it.pagopa.interop.common.controller;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.contract.model.producer_keychain.ProducerKeychain;
import it.pagopa.interop.common.contract.client.ProducerKeychainClient;
import it.pagopa.interop.common.contract.request.ProducerKeychainReadAllRequest;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerKeychainController {

    private final ProducerKeychainClient service;

    @Given("una lista di {int} Producer Keychain( associati al contesto dell'Ente Erogatore)")
    public void createProducerKeychains(int size) {
        for (int i = 0; i < size; i++)
            service
                .create()
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

    }

    @When("(l'utente )legge tutti i Producer Keychain")
    public void getProducerKeychains() {
        Assertions.assertThat(readAllKeychains())
                .as("La lista dei Producer Keychain non deve essere null")
                .isNotNull();
    }

    @When("(l'utente )legge tutti gli {int} Producer Keychain")
    @When("(l'utente )legge tutti i {int} Producer Keychain")
    public void getProducerKeychains(Integer size) {
        Assertions.assertThat(readAllKeychains())
                .as("La lista dei Producer Keychain deve contenere " + size + " elementi")
                .hasSize(size);
    }

    @When("viene inviata una richiesta di eliminazione per ogni Producer Keychain presente nella lista")
    public void deleteProducerKeychains() {
        service.deleteAll();
    }

    @Then("la lista di tutti i Producer Keychain per l'Ente risulta vuota")
    public void verifyProducerKeychainsDeleted() {
        Assertions.assertThat(readAllKeychains())
                .as("La lista dei Producer Keychain deve essere vuota dopo l'eliminazione")
                .isEmpty();
    }

    private List<ProducerKeychain> readAllKeychains() {
        return service
                .readAll(ProducerKeychainReadAllRequest.unfiltered())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .getModels();
    }
}
