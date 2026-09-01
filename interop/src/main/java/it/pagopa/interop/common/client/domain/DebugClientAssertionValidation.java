package it.pagopa.interop.common.client.domain;

import it.pagopa.kernel.domain.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class DebugClientAssertionValidation implements Identifiable {

    public enum Status {
        PASSED,
        FAILED,
        SKIPPED
    }

    @EqualsAndHashCode.Exclude
    private final ClientAssertion clientAssertion;

    @EqualsAndHashCode.Exclude
    @Builder.Default
    private final UUID id = UUID.randomUUID();

    private final ClientAssertionValidation clientAssertionValidation;
    private final PublicKeyValidation publicKeyRetrieve;
    private final SignatureValidation clientAssertionSignatureVerification;
    private final PlatformValidation platformStatesVerification;
    private final DPoPValidation dpopValidation;

    public DebugClientAssertionValidation(
            ClientAssertion clientAssertion,
            UUID id,
            ClientAssertionValidation clientAssertionValidation,
            PublicKeyValidation publicKeyRetrieve,
            SignatureValidation clientAssertionSignatureVerification,
            PlatformValidation platformStatesVerification,
            DPoPValidation dpopValidation
    ) {
        this.clientAssertion = clientAssertion;
        this.id = id != null ? id : UUID.randomUUID();
        this.clientAssertionValidation = clientAssertionValidation;
        this.publicKeyRetrieve = publicKeyRetrieve;
        this.clientAssertionSignatureVerification = clientAssertionSignatureVerification;
        this.platformStatesVerification = platformStatesVerification;
        this.dpopValidation = dpopValidation;
    }

    public DebugClientAssertionValidation(
            ClientAssertion clientAssertion,
            ClientAssertionValidation clientAssertionValidation,
            PublicKeyValidation publicKeyRetrieve,
            SignatureValidation clientAssertionSignatureVerification,
            PlatformValidation platformStatesVerification,
            DPoPValidation dpopValidation
    ) {
        this(
                clientAssertion,
                UUID.randomUUID(),
                clientAssertionValidation,
                publicKeyRetrieve,
                clientAssertionSignatureVerification,
                platformStatesVerification,
                dpopValidation
        );
    }

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

        @Builder
        @Jacksonized
        public ClientAssertionValidation(Status status, boolean success, List<String> errorsCode) {
            super(status, success, errorsCode);
        }

        public ClientAssertionValidation(ValidationResult result) {
            this(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class PublicKeyValidation extends ValidationResult {

        @Builder
        @Jacksonized
        public PublicKeyValidation(Status status, boolean success, List<String> errorsCode) {
            super(status, success, errorsCode);
        }

        public PublicKeyValidation(ValidationResult result) {
            this(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class SignatureValidation extends ValidationResult {

        @Builder
        @Jacksonized
        public SignatureValidation(Status status, boolean success, List<String> errorsCode) {
            super(status, success, errorsCode);
        }

        public SignatureValidation(ValidationResult result) {
            this(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class PlatformValidation extends ValidationResult {

        @Builder
        @Jacksonized
        public PlatformValidation(Status status, boolean success, List<String> errorsCode) {
            super(status, success, errorsCode);
        }

        public PlatformValidation(ValidationResult result) {
            this(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class DPoPValidation extends ValidationResult {

        @Builder
        @Jacksonized
        public DPoPValidation(Status status, boolean success, List<String> errorsCode) {
            super(status, success, errorsCode);
        }

        public DPoPValidation(ValidationResult result) {
            this(result.getStatus(), result.isSuccess(), result.getErrorsCode());
        }
    }

    public boolean isAllPassed() {
        boolean isPassed = true;

        if (clientAssertionValidation != null) {
            isPassed &= clientAssertionValidation.isSuccess();
        }

        if (publicKeyRetrieve != null) {
            isPassed &= publicKeyRetrieve.isSuccess();
        }

        if (clientAssertionSignatureVerification != null) {
            isPassed &= clientAssertionSignatureVerification.isSuccess();
        }

        if (platformStatesVerification != null) {
            isPassed &= platformStatesVerification.isSuccess();
        }

        if (dpopValidation != null) {
            isPassed &= dpopValidation.isSuccess();
        }

        return isPassed;
    }
}