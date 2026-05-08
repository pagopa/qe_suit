package it.pagopa.interop.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ClientAssertionValidationResult {

    public enum Status {
        PASSED,
        FAILED,
        SKIPPED
    }

    private final ClientAssertionValidation clientAssertionValidation;
    private final PublicKeyValidation publicKeyRetrieve;
    private final SignatureValidation clientAssertionSignatureVerification;
    private final PlatformValidation platformStatesVerification;

    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ValidationResult {
        private final Status status;
        private final boolean success;
        private final String errorCode;
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class ClientAssertionValidation extends ValidationResult {
        public ClientAssertionValidation(ValidationResult result) {
            super(result.getStatus(), result.isSuccess(), result.getErrorCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class PublicKeyValidation extends ValidationResult {
        public PublicKeyValidation(ValidationResult result) {
            super(result.getStatus(), result.isSuccess(), result.getErrorCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class SignatureValidation extends ValidationResult {
        public SignatureValidation(ValidationResult result) {
            super(result.getStatus(), result.isSuccess(), result.getErrorCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class PlatformValidation extends ValidationResult {
        public PlatformValidation(ValidationResult result) {
            super(result.getStatus(), result.isSuccess(), result.getErrorCode());
        }
    }
}