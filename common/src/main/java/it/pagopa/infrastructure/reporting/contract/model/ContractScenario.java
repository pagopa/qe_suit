package it.pagopa.infrastructure.reporting.contract.model;

import java.time.Duration;
import java.time.Instant;

public record ContractScenario(
        String uniqueId,
        String displayName,
        ContractScenarioStatus status,
        Instant startedAt,
        Instant finishedAt,
        Duration duration,
        boolean preconditionFailure,
        String failureType,
        String failureMessage,
        String stackTrace,
        String skipReason,
        int discoveryOrder
) {
}
