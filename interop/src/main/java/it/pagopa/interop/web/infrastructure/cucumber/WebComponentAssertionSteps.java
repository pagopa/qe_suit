package it.pagopa.interop.web.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.pagopa.infrastructure.suit.component.Snackbar;
import lombok.RequiredArgsConstructor;

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

    @Then("viene mostrata la snackbar in cui è presente almeno il messaggio di errore {string}")
    public void assertSnackbarContains(String errorMessage) {
        assertThat(snackbar.getErrorMessage())
                .as("Messaggio di errore visualizzato nella snackbar")
                .contains(errorMessage);
    }
}
