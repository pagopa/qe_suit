package it.pagopa.interop.common.infrastructure.reporting.contract;

import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractGroup;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractReport;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractReportChannel;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractScenario;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractScenarioStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

class ContractReportSummaryCalculator {

    ContractReport summarize(List<ContractGroup> groups, Instant generatedAt) {
        List<ContractScenario> allScenarios = groups.stream().flatMap(group -> group.scenarios().stream()).toList();
        int passed = countByStatus(allScenarios, ContractScenarioStatus.PASSED);
        int failed = countByStatus(allScenarios, ContractScenarioStatus.FAILED);
        int skipped = countByStatus(allScenarios, ContractScenarioStatus.SKIPPED);
        int aborted = countByStatus(allScenarios, ContractScenarioStatus.ABORTED);
        int preconditionFailed = (int) allScenarios.stream().filter(ContractScenario::preconditionFailure).count();
        Instant effectiveStart = allScenarios.stream().map(ContractScenario::startedAt).filter(v -> v != null).min(Instant::compareTo).orElse(null);
        Instant effectiveEnd = allScenarios.stream().map(ContractScenario::finishedAt).filter(v -> v != null).max(Instant::compareTo).orElse(null);
        Duration effectiveDuration = duration(effectiveStart, effectiveEnd);
        List<ContractReportChannel> channels = groups.stream()
                .map(group -> new ContractReportChannel(group.channelKey(), group.channelLabel()))
                .distinct()
                .toList();
        return new ContractReport(
                generatedAt,
                effectiveStart,
                effectiveEnd,
                effectiveDuration,
                allScenarios.size(),
                passed,
                failed,
                skipped,
                aborted,
                preconditionFailed,
                channels,
                groups
        );
    }

    private int countByStatus(List<ContractScenario> scenarios, ContractScenarioStatus status) {
        return (int) scenarios.stream().filter(s -> s.status() == status).count();
    }

    private Duration duration(Instant startedAt, Instant finishedAt) {
        if (startedAt == null || finishedAt == null) {
            return Duration.ZERO;
        }
        return Duration.between(startedAt, finishedAt);
    }
}
