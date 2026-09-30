package it.pagopa.interop.common.client.infrastructure.cucumber;

public enum ValidationStep {
    clientAssertionValidation,
    publicKeyRetrieve,
    clientAssertionSignatureVerification,
    platformStatesVerification,
    dpopValidation,
    dpopProofValidation
}
