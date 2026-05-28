package it.pagopa.interop.controller;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.config.parameter_type.mapper.DataTableMapper;
import it.pagopa.interop.domain.context.ClientAssertionContext;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.domain.model.DPoPProof;
import it.pagopa.interop.service.client_assertion.impl.WebDevToolsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DevToolsController {
    private final WebDevToolsService webClientAssertionService;
    private final ClientAssertionContext clientAssertionContext;
    private final DataTableMapper dataTableMapper;

    @When("l'utente richiede la validazione della {currentClientAssertion} associata al {currentClient}")
    public void validateClientAssertion(ClientAssertion clientAssertion, Client client) {
        ClientAssertionValidationResult result = webClientAssertionService.performValidation(clientAssertion, client);
        clientAssertionContext.addValidation(clientAssertion, result);
    }

    @When("l'utente richiede la validazione della {currentClientAssertion} e della {currentDpopProof} associate al {currentClient}")
    public void validateClientAssertion(ClientAssertion clientAssertion, DPoPProof dPoPProof, Client client) {
        ClientAssertionValidationResult result = webClientAssertionService.performValidation(clientAssertion, client, dPoPProof);
        clientAssertionContext.addValidation(clientAssertion, result);
    }

    @When("l'utente invia la form della debug client assertion inserendo:")
    public void validateClientAssertion(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        String rawClientAssertion = data.get("clientAssertion");
        String rawDpopProof = data.get("dpopProof");
        String rawCliendId = data.get("clientId");

        String resolvedClientAssertion = rawClientAssertion != null ? dataTableMapper.resolve(rawClientAssertion) : null;
        String resolvedDpopProof = rawDpopProof != null ? dataTableMapper.resolve(rawDpopProof) : null;
        String resolvedCliendId = rawCliendId != null ? dataTableMapper.resolve(rawCliendId) : null;

        webClientAssertionService.submitValidationRequest(resolvedClientAssertion, resolvedCliendId, resolvedDpopProof);
    }

    @Then("i risultati della validazione della {currentClientAssertion} sono:")
    public void checkValidationResult(ClientAssertion clientAssertion, ClientAssertionValidationResult expected) {
        ClientAssertionValidationResult actual = clientAssertionContext.getValidation(clientAssertion);
        assertThat(actual)
                .as("Validation result for clientAssertion: %s", clientAssertion)
                .isEqualTo(expected);
    }

    @Then("il text field Client assertion viene evidenziato come errore e viene mostrato il messaggio di validazione {string}")
    public void assertClientAssertionInput(String errorMessage) {
        String actualErrorMessage = webClientAssertionService.getClientAssertionInputErrorMessage();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nel campo Client Assertion")
                .isEqualTo(errorMessage);
    }
}
