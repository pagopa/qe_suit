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
import it.pagopa.interop.domain.services.client_assertion.impl.WebClientAssertionService;
import it.pagopa.interop.infrastructure.client.auth.context.user.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientAssertionController {
    private final CreateClientAssertionService clientAssertionService;
    private final WebClientAssertionService webClientAssertionService;
    private final CurrentUserContext currentUserContext;
    private final ClientAssertionContext clientAssertionContext;

    @Given("una client assertion valida generata usando il {currentClient} e la {currentPurpose}")
    public void createClientAssertion(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        String clientAssertion = clientAssertionService.createClientAssertion(client, purpose);
        clientAssertionContext.upsert(new ClientAssertion(clientAssertion));
    }

    @When("l'utente richiede la validazione della {currentClientAssertion} associata al {currentClient}")
    public void validateClientAssertion(ClientAssertion clientAssertion, Client client) {
        webClientAssertionService.validateClientAssertion(clientAssertion, client);
    }

    @Then("i risultati della validazione sono:")
    public void checkValidationResult(){

    }
}
