package it.pagopa.interop.common.infrastructure.reporting.contract.parser;

public record JUnitThrowableData(
        String type,
        String message,
        String stackTrace
) {
}
