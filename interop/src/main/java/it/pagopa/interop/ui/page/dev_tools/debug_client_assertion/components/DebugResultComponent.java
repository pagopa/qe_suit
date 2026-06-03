package it.pagopa.interop.ui.page.dev_tools.debug_client_assertion.components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.common.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.ui.component.Button;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static it.pagopa.interop.common.domain.model.ClientAssertionValidationResult.Status.*;

public interface DebugResultComponent extends Component {

    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//button[span[contains(normalize-space(text()), 'Validazione della client assertion')]]")
    Button clientAssertionValidationResultButton();

    @XPath(".//button[span[contains(normalize-space(text()), 'Recupero della chiave pubblica depositata su PDND Interoperabilità')]]")
    Button publicKetValidationResultButton();

    @XPath(".//button[span[contains(normalize-space(text()), 'Verifica della firma della client assertion')]]")
    Button signatureValidationResultButton();

    @XPath(".//button[span[contains(normalize-space(text()), 'Verifica degli stati')]]")
    Button platformValidationResultButton();

    @XPath(".//button[span[contains(normalize-space(text()), 'Validazione DPoP proof')]]")
    Button dpopProofValidationResultButton();

    DebugDrawer drawer();

    @Override
    default void assertLoaded() {
        title().readAndAssert(title ->
                Assertions.assertThat(title)
                        .isNotBlank()
                        .containsIgnoringCase("Esito del debug")
        );
    }

    default ClientAssertionValidationResult.ClientAssertionValidation getClientAssertionValidation() {
        return new ClientAssertionValidationResult.ClientAssertionValidation(
                readValidationStep(clientAssertionValidationResultButton())
        );
    }

    default ClientAssertionValidationResult.PublicKeyValidation getPublicKeyValidation() {
        return new ClientAssertionValidationResult.PublicKeyValidation(
                readValidationStep(publicKetValidationResultButton())
        );
    }

    default ClientAssertionValidationResult.SignatureValidation getSignatureValidation() {
        return new ClientAssertionValidationResult.SignatureValidation(
                readValidationStep(signatureValidationResultButton())
        );
    }

    default ClientAssertionValidationResult.PlatformValidation getPlatformValidation() {
        return new ClientAssertionValidationResult.PlatformValidation(
                readValidationStep(platformValidationResultButton())
        );
    }

    default ClientAssertionValidationResult.DPoPValidation getDPoPValidation() {
        return new ClientAssertionValidationResult.DPoPValidation(
                readValidationStep(dpopProofValidationResultButton())
        );
    }

    private ClientAssertionValidationResult.ValidationResult readValidationStep(Clickable button) {
        button.click();
        try {
            boolean isSuccess = drawer().result().isSuccess();
            boolean isError = drawer().result().isError();
            boolean isSkipped = drawer().result().isWarning();

            Assertions.assertThat(isSuccess | isError | isSkipped)
                    .as("Faild to retrive validation status")
                    .isTrue();

            ClientAssertionValidationResult.Status status = PASSED;
            final List<String> errorsCode = new ArrayList<>();

            if (isError) {
                status = FAILED;
                errorsCode.addAll(drawer().errorCode().readAll());

                drawer().result().text().readAndAssert(resultText -> {
                    Assertions.assertThat(resultText)
                            .as("Il testo del risultato non deve essere vuoto")
                            .isNotBlank();

                    String expected = errorsCode.size() == 1
                            ? "1 errore"
                            : String.format("%d errori", errorsCode.size());

                    Assertions.assertThat(resultText)
                            .as("Il testo del risultato deve contenere il conteggio errori atteso")
                            .containsIgnoringCase(expected);
                });
            } else if (!isSuccess) {
                status = SKIPPED;
            }

            return new ClientAssertionValidationResult.ValidationResult(status, isSuccess, errorsCode);
        } finally {
            // chiude sempre il drawer, anche in caso di eccezione
            drawer().close();
        }
    }
}
