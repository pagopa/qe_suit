package it.pagopa.interop.common.client.infrastructure.cucumber.dto;

public enum ValidationStep {
    clientAssertionValidation,
    publicKeyRetrieve,
    clientAssertionSignatureVerification,
    platformStatesVerification,
    dpopValidation,
    dpopProofValidation
}
