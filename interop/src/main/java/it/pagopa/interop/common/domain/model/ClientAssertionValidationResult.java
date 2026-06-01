package it.pagopa.interop.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

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
    private final DPoPValidation dpopValidation;

    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ValidationResult {
        private final Status status;
        private final boolean success;
        private final List<String> errorsCode;
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class ClientAssertionValidation extends ValidationResult {
        public ClientAssertionValidation(ValidationResult result) {
            super(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class PublicKeyValidation extends ValidationResult {
        public PublicKeyValidation(ValidationResult result) {
            super(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class SignatureValidation extends ValidationResult {
        public SignatureValidation(ValidationResult result) {
            super(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class PlatformValidation extends ValidationResult {
        public PlatformValidation(ValidationResult result) {
            super(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class DPoPValidation extends ValidationResult {
        public DPoPValidation(ValidationResult result) {
            super(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    public boolean isAllPassed(){
        boolean isPassed = true;

        if(clientAssertionValidation != null) isPassed &= clientAssertionValidation.isSuccess();
        if(publicKeyRetrieve != null) isPassed &= publicKeyRetrieve.isSuccess();
        if(clientAssertionSignatureVerification != null) isPassed &= clientAssertionSignatureVerification.isSuccess();
        if(platformStatesVerification != null) isPassed &= platformStatesVerification.isSuccess();
        if(dpopValidation != null) isPassed &= dpopValidation.isSuccess();

        return isPassed;
    }
}