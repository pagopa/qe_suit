package it.pagopa.interop.common.infrastructure.reporting.contract;

import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractReportConfig;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractGroup;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractReport;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractScenario;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractScenarioStatus;
import it.pagopa.interop.common.infrastructure.reporting.contract.parser.JUnitExecutionNode;
import it.pagopa.interop.common.infrastructure.reporting.contract.parser.JUnitExecutionStatus;
import it.pagopa.interop.common.infrastructure.reporting.contract.resolver.ContractChannelResolver;
import it.pagopa.interop.common.infrastructure.reporting.contract.resolver.ContractFactoryContext;
import it.pagopa.interop.common.infrastructure.reporting.contract.resolver.ContractTargetResolverRegistry;
import it.pagopa.interop.common.infrastructure.reporting.contract.resolver.ResolvedContractChannel;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContractReportBuilder {

    private final ContractTargetResolverRegistry targetResolverRegistry;
    private final ContractReportSummaryCalculator summaryCalculator = new ContractReportSummaryCalculator();

    public ContractReportBuilder(ContractTargetResolverRegistry targetResolverRegistry) {
        this.targetResolverRegistry = targetResolverRegistry;
    }

    public ContractReport build(List<JUnitExecutionNode> nodes, ContractReportConfig config, Instant generatedAt) {
        ContractChannelResolver channelResolver = new ContractChannelResolver(config);
        Map<String, JUnitExecutionNode> byId = nodes.stream().collect(LinkedHashMap::new, (map, node) -> map.put(node.getId(), node), Map::putAll);
        List<ContractGroup> groups = new ArrayList<>();
        for (JUnitExecutionNode factory : nodes) {
            if (!isFactory(factory)) {
                continue;
            }
            String className = resolveClassName(factory, byId);
            String methodName = resolveMethodName(factory);
            Optional<ResolvedContractChannel> channel = channelResolver.resolveByClassName(simpleName(className));
            if (channel.isEmpty()) {
                continue;
            }
            String target = targetResolverRegistry.get(channel.get().config().targetType())
                    .resolveTarget(new ContractFactoryContext(channel.get().config(), className, methodName));
            List<ContractScenario> scenarios = buildScenarios(factory, methodName);
            if (scenarios.isEmpty()) {
                continue;
            }
            ContractScenarioStatus aggregateStatus = aggregateStatus(scenarios);
            String searchText = buildSearchText(channel.get().config().label(), target, methodName, className, scenarios);
            groups.add(new ContractGroup(
                    channel.get().key(),
                    channel.get().config().label(),
                    channel.get().config().targetType(),
                    target,
                    className,
                    methodName,
                    searchText,
                    aggregateStatus,
                    scenarios.size(),
                    scenarios
            ));
        }
        groups.sort(Comparator.comparing(ContractGroup::channelLabel).thenComparing(ContractGroup::methodName));
        return summaryCalculator.summarize(groups, generatedAt);
    }

    private List<ContractScenario> buildScenarios(JUnitExecutionNode factory, String methodName) {
        List<JUnitExecutionNode> dynamicChildren = factory.getChildren().stream()
                .filter(this::isDynamicScenario)
                .toList();
        List<ContractScenario> scenarios = new ArrayList<>();
        if (dynamicChildren.isEmpty() && factory.getStatus() == JUnitExecutionStatus.FAILED) {
            scenarios.add(new ContractScenario(
                    factory.getUniqueId(),
                    "Precondition failed",
                    ContractScenarioStatus.FAILED,
                    factory.getStartedAt(),
                    factory.getFinishedAt(),
                    duration(factory.getStartedAt(), factory.getFinishedAt()),
                    true,
                    factory.getThrowable() == null ? null : factory.getThrowable().type(),
                    factory.getThrowable() == null ? null : factory.getThrowable().message(),
                    factory.getThrowable() == null ? null : factory.getThrowable().stackTrace(),
                    null,
                    0
            ));
            return scenarios;
        }
        int order = 0;
        for (JUnitExecutionNode child : dynamicChildren) {
            scenarios.add(new ContractScenario(
                    child.getUniqueId(),
                    child.getDisplayName(),
                    mapStatus(child.getStatus()),
                    child.getStartedAt(),
                    child.getFinishedAt(),
                    duration(child.getStartedAt(), child.getFinishedAt()),
                    false,
                    child.getThrowable() == null ? null : child.getThrowable().type(),
                    child.getThrowable() == null ? null : child.getThrowable().message(),
                    child.getThrowable() == null ? null : child.getThrowable().stackTrace(),
                    child.getSkipReason(),
                    order++
            ));
        }
        return scenarios;
    }

    private String resolveClassName(JUnitExecutionNode node, Map<String, JUnitExecutionNode> byId) {
        JUnitExecutionNode cursor = node;
        while (cursor != null) {
            if (cursor.getClassName() != null && !cursor.getClassName().isBlank()) {
                return cursor.getClassName();
            }
            cursor = cursor.getParentId() == null ? null : byId.get(cursor.getParentId());
        }
        throw new IllegalStateException("Cannot resolve className for node: " + node.getUniqueId());
    }

    private String resolveMethodName(JUnitExecutionNode node) {
        if (node.getMethodName() != null && !node.getMethodName().isBlank()) {
            return node.getMethodName();
        }
        String uniqueId = node.getUniqueId();
        if (uniqueId == null || uniqueId.isBlank()) {
            throw new IllegalStateException("Cannot resolve TestFactory method name from uniqueId");
        }
        int start = uniqueId.indexOf("[test-factory:");
        int end = uniqueId.indexOf(']', start);
        if (start < 0 || end < 0) {
            throw new IllegalStateException("Cannot resolve TestFactory method name from uniqueId: " + uniqueId);
        }
        String raw = uniqueId.substring(start + "[test-factory:".length(), end).trim();
        return raw.endsWith("()") ? raw.substring(0, raw.length() - 2) : raw;
    }

    private String simpleName(String className) {
        int idx = className.lastIndexOf('.');
        return idx >= 0 ? className.substring(idx + 1) : className;
    }

    private String buildSearchText(String channel, String target, String method, String className, List<ContractScenario> scenarios) {
        String scenarioNames = scenarios.stream().map(ContractScenario::displayName).reduce("", (left, right) -> left + " " + right);
        return (channel + " " + target + " " + method + " " + className + " " + scenarioNames).toLowerCase();
    }

    private boolean isFactory(JUnitExecutionNode node) {
        String uniqueId = node.getUniqueId();
        return uniqueId != null && uniqueId.contains("[test-factory:") && !uniqueId.contains("[dynamic-test:");
    }

    private boolean isDynamicScenario(JUnitExecutionNode node) {
        String uniqueId = node.getUniqueId();
        return uniqueId != null && uniqueId.contains("[dynamic-test:");
    }

    private ContractScenarioStatus mapStatus(JUnitExecutionStatus status) {
        return switch (status) {
            case SUCCESSFUL -> ContractScenarioStatus.PASSED;
            case SKIPPED -> ContractScenarioStatus.SKIPPED;
            case ABORTED -> ContractScenarioStatus.ABORTED;
            case FAILED, ERRORED, UNKNOWN -> ContractScenarioStatus.FAILED;
        };
    }

    private ContractScenarioStatus aggregateStatus(List<ContractScenario> scenarios) {
        if (scenarios.stream().anyMatch(s -> s.status() == ContractScenarioStatus.FAILED)) {
            return ContractScenarioStatus.FAILED;
        }
        if (scenarios.stream().anyMatch(s -> s.status() == ContractScenarioStatus.ABORTED)) {
            return ContractScenarioStatus.ABORTED;
        }
        if (scenarios.stream().allMatch(s -> s.status() == ContractScenarioStatus.SKIPPED)) {
            return ContractScenarioStatus.SKIPPED;
        }
        return ContractScenarioStatus.PASSED;
    }

    private Duration duration(Instant startedAt, Instant finishedAt) {
        if (startedAt == null || finishedAt == null) {
            return Duration.ZERO;
        }
        return Duration.between(startedAt, finishedAt);
    }
}
