package it.pagopa.interop.controller;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.domain.context.ClientAssertionContext;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.domain.services.client_assertion.CreateClientAssertionService;
import it.pagopa.interop.domain.services.client_assertion.impl.WebClientAssertionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientAssertionController {
    private final CreateClientAssertionService clientAssertionService;
    private final WebClientAssertionService webClientAssertionService;
    private final ClientAssertionContext clientAssertionContext;

    @Given("una client assertion valida generata usando il {currentClient} e la {currentPurpose}")
    public void createClientAssertion(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        String clientAssertion = clientAssertionService.createClientAssertion(client, purpose);
        clientAssertionContext.upsert(new ClientAssertion(clientAssertion));
    }

    @When("l'utente richiede la validazione della {currentClientAssertion} associata al {currentClient}")
    public void validateClientAssertion(ClientAssertion clientAssertion, Client client) {
        ClientAssertionValidationResult result =
                webClientAssertionService.validateClientAssertion(clientAssertion, client);

        clientAssertionContext.addValidation(clientAssertion, result);
    }


    @Then("i risultati della validazione della {currentClientAssertion} sono:")
    public void checkValidationResult(ClientAssertion clientAssertion, ClientAssertionValidationResult expected) {
        ClientAssertionValidationResult actual = clientAssertionContext.getValidation(clientAssertion);

        assertThat(actual)
                .as("Validation result for clientAssertion: %s", clientAssertion)
                .isEqualTo(expected);
    }
}
