package it.pagopa.interop.common.client.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.application.command.DebugClientAssertionCommand;
import it.pagopa.interop.common.client.application.DebugClientAssertionUseCase;
import it.pagopa.interop.common.client.domain.DebugClientAssertionValidation;
import it.pagopa.interop.common.kernel.context.EntityStore;
import it.pagopa.interop.common.kernel.security.DPoPProof;
import it.pagopa.interop.common.client.domain.ClientAssertion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DebugClientAssertionSteps {
    private final DebugClientAssertionUseCase debugClientAssertionUseCase;
    private final EntityStore entityStore;

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

    @When("l'utente inoltra la richiesta di validazione specificando {currentClientAssertion} e {currentClient}")
    public void executeClientAssertionValidation(ClientAssertion clientAssertion, Client client) {
        executeClientAssertionValidation(
                DebugClientAssertionCommand.builder()
                        .clientAssertion(clientAssertion)
                        .client(client)
                        .build()
        );
    }

    @When("l'utente invia la form della debug client assertion inserendo:")
    public void executeClientAssertionValidation(DebugClientAssertionCommand formData) {
        debugClientAssertionUseCase.executeClientAssertionValidation(formData);
    }

    @Then("i risultati della validazione della {currentClientAssertion} sono:")
    public void checkValidationResult(ClientAssertion clientAssertion, List<ValidationExpected> expected) {
        DebugClientAssertionValidation actual = entityStore.find(
                        DebugClientAssertionValidation.class,
                        validation -> validation.getClientAssertion().getClientAssertion().equals(clientAssertion.getClientAssertion())
                )
                .orElseThrow(() -> new AssertionError("Client assertion validation not found"));

        expected.forEach(validation -> {
            DebugClientAssertionValidation.ValidationResult actualResult =
                    getValidationResult(actual, validation.step());

            assertThat(actualResult)
                    .as(
                            "Validation result for step '%s' of clientAssertion: %s",
                            validation.step(),
                            clientAssertion
                    )
                    .isNotNull();

            assertThat(actualResult.getStatus())
                    .as("Status for validation step '%s'", validation.step())
                    .isEqualTo(validation.result());

            assertThat(actualResult.getErrorsCode())
                    .as("Errors for validation step '%s'", validation.step())
                    .containsExactlyElementsOf(validation.errorsAsList());
        });
    }

    private DebugClientAssertionValidation.ValidationResult getValidationResult(DebugClientAssertionValidation validation, ValidationStep step) {
        return switch (step) {
            case clientAssertionValidation -> validation.getClientAssertionValidation();
            case publicKeyRetrieve -> validation.getPublicKeyRetrieve();
            case clientAssertionSignatureVerification -> validation.getClientAssertionSignatureVerification();
            case platformStatesVerification -> validation.getPlatformStatesVerification();
            case dpopValidation, dpopProofValidation -> validation.getDpopValidation();
        };
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
