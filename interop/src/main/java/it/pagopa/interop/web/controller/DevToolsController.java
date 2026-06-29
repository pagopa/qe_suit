package it.pagopa.interop.web.controller;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.cucumber.parameter_type.mapper.DataTableMapper;
import it.pagopa.interop.common.contract.model.client.Client;
import it.pagopa.interop.common.contract.model.client.ClientAssertion;
import it.pagopa.interop.common.contract.model.client.DPoPProof;
import it.pagopa.interop.common.contract.model.response.DebugClientAssertionValidationResponse;
import it.pagopa.interop.web.service.DevToolsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DevToolsController {
    private final DevToolsService webClientAssertionService;
    private final ScenarioContext scenarioContext;
    private final DataTableMapper dataTableMapper;

    @When("l'utente inoltra la richiesta di validazione specificando {currentClientAssertion}(, {currentDpopProof}) e {currentClient}")
    public void validateClientAssertion(ClientAssertion clientAssertion, DPoPProof dPoPProof, Client client) {
        DebugClientAssertionValidationResponse result = webClientAssertionService.performValidation(clientAssertion, client, dPoPProof);
        scenarioContext.upsert(result);
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
    public void checkValidationResult(ClientAssertion clientAssertion, DebugClientAssertionValidationResponse expected) {
        DebugClientAssertionValidationResponse actual = scenarioContext.find(
                DebugClientAssertionValidationResponse.class,
                validation -> validation.getClientAssertion().getId().equals(clientAssertion.getId())
                )
                .orElseThrow(() -> new AssertionError("Client assertion validation not found"));

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
