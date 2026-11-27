package it.pagopa.interop.web.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.pagopa.infrastructure.suit.component.Snackbar;
import it.pagopa.interop.common.infrastructure.copy.ExpectedErrorMessages;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class WebComponentAssertionSteps {

    private final Snackbar snackbar;

    @Then("viene mostrata la snackbar con un messaggio di successo {string}")
    public void assertSnackbarSuccess(String successMessage) {
        assertThat(snackbar.getSuccessMessage())
                .as("Messaggio di successo visualizzato nella snackbar")
                .isEqualTo(successMessage);
    }

    @Then("viene mostrata la snackbar con un messaggio di errore {string}")
    public void assertSnackbar(String errorMessage) {
        assertThat(snackbar.getErrorMessage())
                .as("Messaggio di errore visualizzato nella snackbar")
                .isEqualTo(errorMessage);
    }

    @Then("viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo {string} in lingua {string}")
    public void assertSnackbarErrorFromCatalog(String messageId, String language) {
        String expectedMessage = ExpectedErrorMessages.load().render(messageId, language, Map.of());
        assertThat(snackbar.get(Gettable.FindPolicy.VISIBLE))
                .as("Snackbar visibile per il messaggio %s in lingua %s", messageId, language)
                .isPresent();
        assertSnackbar(expectedMessage);
    }

    @Then("viene mostrata la snackbar in cui è presente almeno il messaggio di errore {string}")
    public void assertSnackbarContains(String errorMessage) {
        assertThat(snackbar.getErrorMessage())
                .as("Messaggio di errore visualizzato nella snackbar")
                .contains(errorMessage);
    }
}
