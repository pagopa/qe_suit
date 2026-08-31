package it.pagopa.interop.common.infrastructure.contract.http;

final class ContractHttpException extends RuntimeException {
    ContractHttpException(String message) {
        super(message);
    }

    ContractHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
