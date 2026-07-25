package it.pagopa.interop.common.debug_client_assertion.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.debug_client_assertion.application.DebugClientAssertionCommand;
import it.pagopa.interop.common.debug_client_assertion.application.DebugClientAssertionUseCase;
import it.pagopa.interop.common.debug_client_assertion.domain.DebugClientAssertionValidation;
import it.pagopa.interop.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.common.infrastructure.security.jwt.DPoPProof;
import it.pagopa.interop.common.kernel.security.ClientAssertion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DebugClientAssertionSteps {
    private final DebugClientAssertionUseCase debugClientAssertionUseCase;
    private final DomainContext domainContext;

    @When("l'utente inoltra la richiesta di validazione specificando {currentClientAssertion}, {currentDpopProof} e {currentClient}")
    public void executeClientAssertionValidation(ClientAssertion clientAssertion, DPoPProof dPoPProof, Client client) {
        executeClientAssertionValidation(
                DebugClientAssertionCommand.builder()
                        .clientAssertion(clientAssertion)
                        .client(client)
                        .dpopProof(dPoPProof)
                        .build()
        );
    }

    @When("l'utente invia la form della debug client assertion inserendo:")
    public void executeClientAssertionValidation(DebugClientAssertionCommand formData) {
        debugClientAssertionUseCase.executeClientAssertionValidation(formData);
    }

    @Then("i risultati della validazione della {currentClientAssertion} sono:")
    public void checkValidationResult(ClientAssertion clientAssertion, DebugClientAssertionValidation expected) {
        DebugClientAssertionValidation actual = domainContext.find(
                        DebugClientAssertionValidation.class,
                        validation -> validation.getClientAssertion().getId().equals(clientAssertion.getId())
                )
                .orElseThrow(() -> new AssertionError("Client assertion validation not found"));

        assertThat(actual)
                .as("Validation result for clientAssertion: %s", clientAssertion)
                .isEqualTo(expected);
    }

    //TODO: questo è un test di contratto a va gestito diversamente
//    @Then("il text field Client assertion viene evidenziato come errore e viene mostrato il messaggio di validazione {string}")
//    public void assertClientAssertionInput(String errorMessage) {
//        String actualErrorMessage = debugClientAssertionGateway.getClientAssertionInputErrorMessage();
//
//        assertThat(actualErrorMessage)
//                .as("Messaggio di errore visualizzato nel campo Client Assertion")
//                .isEqualTo(errorMessage);
//    }
}
