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

    private ClientAssertionValidation clientAssertionValidation;
    private PublicKeyValidation publicKeyRetrieve;
    private SignatureValidation clientAssertionSignatureVerification;
    private PlatformValidation platformStatesVerification;

    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ValidationResult {
        private boolean success;
        private String errorCode;
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class ClientAssertionValidation extends ValidationResult {
        public ClientAssertionValidation(ValidationResult result) {
            super(result.isSuccess(), result.getErrorCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class PublicKeyValidation extends ValidationResult {
        public PublicKeyValidation(ValidationResult result) {
            super(result.isSuccess(), result.getErrorCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class SignatureValidation extends ValidationResult {
        public SignatureValidation(ValidationResult result) {
            super(result.isSuccess(), result.getErrorCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class PlatformValidation extends ValidationResult {
        public PlatformValidation(ValidationResult result) {
            super(result.isSuccess(), result.getErrorCode());
        }
    }
}