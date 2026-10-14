package it.pagopa.infrastructure.reporting.contract.model;

import it.pagopa.infrastructure.reporting.contract.config.ContractTargetType;

import java.util.List;

public record ContractGroup(
        String channelKey,
        String channelLabel,
        ContractTargetType targetType,
        String target,
        String contractClassName,
        String methodName,
        String searchText,
        ContractScenarioStatus aggregateStatus,
        int scenarioCount,
        List<ContractScenario> scenarios
) {
}
