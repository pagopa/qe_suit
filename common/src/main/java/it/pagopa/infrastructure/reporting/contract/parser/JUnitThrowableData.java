package it.pagopa.infrastructure.reporting.contract.parser;

public record JUnitThrowableData(
        String type,
        String message,
        String stackTrace
) {
}
