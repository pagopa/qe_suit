package it.pagopa.interop.web.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.pagopa.interop.web.infrastructure.WebCommonGateway;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class WebComponentAssertionSteps {

    private final WebCommonGateway webCommonGateway;

    @Then("viene mostrata la snackbar con un messaggio di successo {string}")
    public void assertSnackbarSuccess(String successMessage) {
        assertThat(webCommonGateway.getSnackbarSuccessMessage())
                .as("Messaggio di successo visualizzato nella snackbar")
                .isEqualTo(successMessage);
    }

    @Then("viene mostrata la snackbar con un messaggio di errore contenente {string}")
    public void assertSnackbar(String errorMessage) {
        assertThat(webCommonGateway.getSnackbarErrorMessage())
                .as("Messaggio di errore visualizzato nella snackbar")
                .contains(errorMessage);
    }
}
