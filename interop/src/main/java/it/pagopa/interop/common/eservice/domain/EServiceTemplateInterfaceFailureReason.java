package it.pagopa.interop.common.eservice.domain;

/**
 * Business-readable explanation; until the BFF exposes reason-specific failures, both cases expect HTTP 400.
 */
public enum EServiceTemplateInterfaceFailureReason {
    URL_COUNT_MISMATCH,
    URL_NOT_ALLOWED
}
