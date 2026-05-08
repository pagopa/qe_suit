package it.pagopa.interop.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientAssertionValidationResult extends AbstractModel {

    @Getter
    public static class ValidationResult {
        private final boolean isValid;
        private final String errorCode;

        public ValidationResult(ValidationResult result) {
            this.isValid = result.isValid;
            this.errorCode = result.errorCode;
        }

        public ValidationResult(boolean isValid, String errorCode) {
            this.isValid = isValid;
            this.errorCode = errorCode;
        }
    }

    public static class ClientAssertionValidation extends ValidationResult {

        public ClientAssertionValidation(ValidationResult result) {
            super(result);
        }
    }

    public static class PublicKeyValidation extends ValidationResult {

        public PublicKeyValidation(ValidationResult result) {
            super(result);
        }
    }

    public static class SignatureValidation extends ValidationResult {

        public SignatureValidation(ValidationResult result) {
            super(result);
        }
    }

    public static class PlatformValidation extends ValidationResult {

        public PlatformValidation(ValidationResult result) {
            super(result);
        }
    }

    private final ClientAssertionValidation clientAssertionValidation;
    private final PublicKeyValidation publicKeyValidation;
    private final SignatureValidation signatureValidation;
    private final PlatformValidation platformValidation;

    @Override
    public String getUniqueIdentifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
