package it.pagopa.interop.new_arch.web.debug_client_assertion.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.new_arch.common.client.domain.Client;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.infrastructure.security.jwt.DPoPProof;
import it.pagopa.interop.new_arch.common.kernel.security.ClientAssertion;
import it.pagopa.interop.new_arch.web.debug_client_assertion.application.ClientAssertionFormData;
import it.pagopa.interop.new_arch.web.debug_client_assertion.application.DebugClientAssertionValidationResponse;
import it.pagopa.interop.new_arch.web.debug_client_assertion.infrastructure.WebDebugClientAssertionGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebDebugClientAssertionSteps {
    private final WebDebugClientAssertionGateway debugClientAssertionGateway;
    private final DomainContext domainContext;

    @When("l'utente inoltra la richiesta di validazione specificando {currentClientAssertion}(, {currentDpopProof}) e {currentClient}")
    public void validateClientAssertion(ClientAssertion clientAssertion, DPoPProof dPoPProof, Client client) {
        DebugClientAssertionValidationResponse result = debugClientAssertionGateway.performValidation(clientAssertion, client, dPoPProof);
        domainContext.upsert(result);
    }

    @When("l'utente invia la form della debug client assertion inserendo:")
    public void validateClientAssertion(ClientAssertionFormData formData) {
        debugClientAssertionGateway.submitValidationRequest(
                formData.getClientAssertion(),
                formData.getClientId(),
                formData.getDpopProof()
        );
    }

    @Then("i risultati della validazione della {currentClientAssertion} sono:")
    public void checkValidationResult(ClientAssertion clientAssertion, DebugClientAssertionValidationResponse expected) {
        DebugClientAssertionValidationResponse actual = domainContext.find(
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
        String actualErrorMessage = debugClientAssertionGateway.getClientAssertionInputErrorMessage();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nel campo Client Assertion")
                .isEqualTo(errorMessage);
    }
}
