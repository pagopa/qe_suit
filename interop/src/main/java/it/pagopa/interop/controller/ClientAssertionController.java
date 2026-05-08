package it.pagopa.interop.controller;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.domain.context.ClientAssertionContext;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.services.client_assertion.CreateClientAssertionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientAssertionController {
    private final CreateClientAssertionService clientAssertionService;
    private final ClientAssertionContext clientAssertionContext;

    @Given("una client assertion valida generata usando il {currentClient} e la {currentPurpose}")
    public void createClientAssertion(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        String clientAssertion = clientAssertionService.createClientAssertion(client, purpose);
        clientAssertionContext.upsert(new ClientAssertion(clientAssertion));
    }

    @When("{currentUser} richiede la validazione della {currentClientAssertion}")
    public void validateClientAssertion(User currentUser, ClientAssertion clientAssertion){

    }

    @Then("i risultati della validazione sono:")
    public void checkValidationResult(){

    }
}
