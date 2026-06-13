package it.pagopa.interop.web.page.dev_tools.debug_client_assertion.components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.common.domain.model.VoucherRequestValidationResult;
import it.pagopa.interop.web.component.Button;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static it.pagopa.interop.common.domain.model.VoucherRequestValidationResult.Status.*;

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

    default VoucherRequestValidationResult.ClientAssertionValidation getClientAssertionValidation() {
        return new VoucherRequestValidationResult.ClientAssertionValidation(
                readValidationStep(clientAssertionValidationResultButton())
        );
    }

    default VoucherRequestValidationResult.PublicKeyValidation getPublicKeyValidation() {
        return new VoucherRequestValidationResult.PublicKeyValidation(
                readValidationStep(publicKetValidationResultButton())
        );
    }

    default VoucherRequestValidationResult.SignatureValidation getSignatureValidation() {
        return new VoucherRequestValidationResult.SignatureValidation(
                readValidationStep(signatureValidationResultButton())
        );
    }

    default VoucherRequestValidationResult.PlatformValidation getPlatformValidation() {
        return new VoucherRequestValidationResult.PlatformValidation(
                readValidationStep(platformValidationResultButton())
        );
    }

    default VoucherRequestValidationResult.DPoPValidation getDPoPValidation() {
        return new VoucherRequestValidationResult.DPoPValidation(
                readValidationStep(dpopProofValidationResultButton())
        );
    }

    private VoucherRequestValidationResult.ValidationResult readValidationStep(Clickable button) {
        button.click();
        try {
            boolean isSuccess = drawer().result().isSuccess();
            boolean isError = drawer().result().isError();
            boolean isSkipped = drawer().result().isWarning();

            Assertions.assertThat(isSuccess | isError | isSkipped)
                    .as("Faild to retrive validation status")
                    .isTrue();

            VoucherRequestValidationResult.Status status = PASSED;
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

            return new VoucherRequestValidationResult.ValidationResult(status, isSuccess, errorsCode);
        } finally {
            // chiude sempre il drawer, anche in caso di eccezione
            drawer().close();
        }
    }
}
