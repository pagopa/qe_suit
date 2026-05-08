package it.pagopa.interop.controller;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.services.client_assertion.ClientAssertionService;
import it.pagopa.interop.utils.KeyPairUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientAssertionController {
    private final ClientAssertionService clientAssertionService;
    private String currentClientAssertion;

    @Given("una client assertion valida generata usando il {currentClient} e la {currentPurpose}")
    public void createClientAssertion(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        currentClientAssertion = clientAssertionService.createClientAssertion(client, purpose);
    }

    @When("{currentUser} richiede la validazione della {currentClientAssertion}")
    public void validateClientAssertion(User currentUser, ClientAssertion clientAssertion){

    }

    @Then("i risultati della validazione sono:")
    public void checkValidationResult(){

    }
}
