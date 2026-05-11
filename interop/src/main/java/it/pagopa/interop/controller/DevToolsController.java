package it.pagopa.interop.controller;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.domain.context.ClientAssertionContext;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.domain.services.client_assertion.impl.WebDevToolsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DevToolsController {
    private final WebDevToolsService webClientAssertionService;
    private final ClientAssertionContext clientAssertionContext;

    @When("l'utente richiede la validazione della {currentClientAssertion} associata al {currentClient}")
    public void validateClientAssertion(ClientAssertion clientAssertion, Client client) {
        ClientAssertionValidationResult result =
                webClientAssertionService.validate(clientAssertion, client);

        clientAssertionContext.addValidation(clientAssertion, result);
    }


    @Then("i risultati della validazione della {currentClientAssertion} sono:")
    public void checkValidationResult(ClientAssertion clientAssertion, ClientAssertionValidationResult expected) {
        ClientAssertionValidationResult actual = clientAssertionContext.getValidation(clientAssertion);

        //TODO: da eliminare i log di debug
        System.out.println("EXPECTED errorsCode: " + expected.getClientAssertionValidation().getErrorsCode());
        System.out.println("ACTUAL errorsCode:   " + actual.getClientAssertionValidation().getErrorsCode());


        assertThat(actual)
                .as("Validation result for clientAssertion: %s", clientAssertion)
                .isEqualTo(expected);
    }

}
