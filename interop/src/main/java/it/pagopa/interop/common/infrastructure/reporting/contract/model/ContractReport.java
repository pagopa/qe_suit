package it.pagopa.interop.common.infrastructure.reporting.contract.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public record ContractReport(
        Instant generatedAt,
        Instant startedAt,
        Instant finishedAt,
        Duration effectiveDuration,
        int total,
        int passed,
        int failed,
        int skipped,
        int aborted,
        int preconditionFailedCount,
        List<ContractReportChannel> channels,
        List<ContractGroup> groups
) {
}
