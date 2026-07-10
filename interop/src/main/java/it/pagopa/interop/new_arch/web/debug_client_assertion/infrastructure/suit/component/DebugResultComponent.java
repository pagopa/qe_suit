package it.pagopa.interop.new_arch.web.debug_client_assertion.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.new_arch.web.debug_client_assertion.application.DebugClientAssertionValidationResponse;
import it.pagopa.interop.new_arch.web.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static it.pagopa.interop.new_arch.web.debug_client_assertion.application.DebugClientAssertionValidationResponse.Status.*;

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

    default DebugClientAssertionValidationResponse.ClientAssertionValidation getClientAssertionValidation() {
        return new DebugClientAssertionValidationResponse.ClientAssertionValidation(
                readValidationStep(clientAssertionValidationResultButton())
        );
    }

    default DebugClientAssertionValidationResponse.PublicKeyValidation getPublicKeyValidation() {
        return new DebugClientAssertionValidationResponse.PublicKeyValidation(
                readValidationStep(publicKetValidationResultButton())
        );
    }

    default DebugClientAssertionValidationResponse.SignatureValidation getSignatureValidation() {
        return new DebugClientAssertionValidationResponse.SignatureValidation(
                readValidationStep(signatureValidationResultButton())
        );
    }

    default DebugClientAssertionValidationResponse.PlatformValidation getPlatformValidation() {
        return new DebugClientAssertionValidationResponse.PlatformValidation(
                readValidationStep(platformValidationResultButton())
        );
    }

    default DebugClientAssertionValidationResponse.DPoPValidation getDPoPValidation() {
        return new DebugClientAssertionValidationResponse.DPoPValidation(
                readValidationStep(dpopProofValidationResultButton())
        );
    }

    private DebugClientAssertionValidationResponse.ValidationResult readValidationStep(Clickable button) {
        button.click();
        try {
            boolean isSuccess = drawer().result().isSuccess();
            boolean isError = drawer().result().isError();
            boolean isSkipped = drawer().result().isWarning();

            Assertions.assertThat(isSuccess | isError | isSkipped)
                    .as("Faild to retrive validation status")
                    .isTrue();

            DebugClientAssertionValidationResponse.Status status = PASSED;
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

            return new DebugClientAssertionValidationResponse.ValidationResult(status, isSuccess, errorsCode);
        } finally {
            // chiude sempre il drawer, anche in caso di eccezione
            drawer().close();
        }
    }
}
