package it.pagopa.interop.domain.web.pages.dev_tools.components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import org.assertj.core.api.Assertions;

import static it.pagopa.interop.domain.model.ClientAssertionValidationResult.Status.*;

public interface DebugResultComponent extends Component {

    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//button[1]")
    Clickable clientAssertionValidationResultButton();

    @XPath(".//button[2]")
    Clickable publicKetValidationResultButton();

    @XPath(".//button[3]")
    Clickable signatureValidationResultButton();

    @XPath(".//button[4]")
    Clickable platformValidationResultButton();

    DebugDrawer drawer();

    @Override
    default void assertLoaded() {
        title().readAndAssert(title ->
                Assertions.assertThat(title)
                        .isNotBlank()
                        .containsIgnoringCase("Esito del debug")
        );
    }

    default ClientAssertionValidationResult getValidationResults() {
        var clientAssertionValidation = new ClientAssertionValidationResult.ClientAssertionValidation(
                readValidationStep(clientAssertionValidationResultButton())
        );

        var publicKeyValidation = new ClientAssertionValidationResult.PublicKeyValidation(
                readValidationStep(publicKetValidationResultButton())
        );

        var signatureValidation = new ClientAssertionValidationResult.SignatureValidation(
                readValidationStep(signatureValidationResultButton())
        );

        var platformValidation = new ClientAssertionValidationResult.PlatformValidation(
                readValidationStep(platformValidationResultButton()));

        return new ClientAssertionValidationResult(
                clientAssertionValidation,
                publicKeyValidation,
                signatureValidation,
                platformValidation
        );
    }

    private ClientAssertionValidationResult.ValidationResult readValidationStep(Clickable button) {
        button.click();
        try {
            boolean isSuccess = drawer().result().isSuccess();
            boolean isError = drawer().result().isError();
            boolean isSkipped = drawer().result().isWarning();

            Assertions.assertThat(isSuccess|isError|isSkipped)
                    .as("Faild to retrive validation status")
                    .isTrue();

            ClientAssertionValidationResult.Status status = PASSED;
            String errorCode = null;

            if (isError) {
                status = FAILED;
                errorCode = drawer().errorCode().read();
            } else if (!isSuccess) {
                status = SKIPPED;
            }

            return new ClientAssertionValidationResult.ValidationResult(status, isSuccess, errorCode);
        } finally {
            // chiude sempre il drawer, anche in caso di eccezione
            drawer().close();
        }
    }
}
