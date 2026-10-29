package it.pagopa.infrastructure.reporting.contract;

import it.pagopa.infrastructure.reporting.contract.config.ContractReportConfig;
import it.pagopa.infrastructure.reporting.contract.model.ContractGroup;
import it.pagopa.infrastructure.reporting.contract.model.ContractReport;
import it.pagopa.infrastructure.reporting.contract.model.ContractScenario;
import it.pagopa.infrastructure.reporting.contract.model.ContractScenarioStatus;
import it.pagopa.infrastructure.reporting.contract.parser.JUnitExecutionNode;
import it.pagopa.infrastructure.reporting.contract.parser.JUnitExecutionStatus;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractChannelResolver;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractFactoryContext;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractTargetResolverRegistry;
import it.pagopa.infrastructure.reporting.contract.resolver.ResolvedContractChannel;
import it.pagopa.infrastructure.reporting.contract.target.ContractCaseTargetRegistry;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class ContractReportBuilder {

    private static final String UNRESOLVED_CASE_TARGET = "API not resolved (operation not created)";

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
            if (isPerCaseTargetFactory(factory)) {
                List<ContractScenario> scenarios = buildScenarios(factory, methodName);
                for (Map.Entry<String, List<ContractScenario>> entry : groupByCaseTarget(factory, scenarios).entrySet()) {
                    groups.add(toGroup(channel.get(), entry.getKey(), className, methodName, entry.getValue()));
                }
                continue;
            }
            String target = targetResolverRegistry.get(channel.get().config().targetType())
                    .resolveTarget(new ContractFactoryContext(channel.get().config(), className, methodName));
            List<ContractScenario> scenarios = buildScenarios(factory, methodName);
            if (scenarios.isEmpty()) {
                continue;
            }
            groups.add(toGroup(channel.get(), target, className, methodName, scenarios));
        }
        groups.sort(Comparator.comparing(ContractGroup::channelLabel).thenComparing(ContractGroup::methodName));
        return summaryCalculator.summarize(groups, generatedAt);
    }

    private ContractGroup toGroup(
            ResolvedContractChannel channel,
            String target,
            String className,
            String methodName,
            List<ContractScenario> scenarios
    ) {
        return new ContractGroup(
                channel.key(),
                channel.config().label(),
                channel.config().targetType(),
                target,
                className,
                methodName,
                buildSearchText(channel.config().label(), target, methodName, className, scenarios),
                aggregateStatus(scenarios),
                scenarios.size(),
                scenarios
        );
    }

    /**
     * I @TestFactory di autorizzazione contengono più API: il target è del singolo caso dinamico
     * e non del nome del metodo.
     */
    private boolean isPerCaseTargetFactory(JUnitExecutionNode factory) {
        return factory.getChildren().stream()
                .filter(this::isDynamicScenario)
                .anyMatch(child -> ContractCaseTargetRegistry.caseId(child.getSourceUri()).isPresent());
    }

    private Map<String, List<ContractScenario>> groupByCaseTarget(JUnitExecutionNode factory, List<ContractScenario> scenarios) {
        Map<String, String> targetByScenarioId = new HashMap<>();
        for (JUnitExecutionNode child : factory.getChildren()) {
            targetByScenarioId.put(
                    child.getUniqueId(),
                    ContractCaseTargetRegistry.caseId(child.getSourceUri())
                            .flatMap(ContractCaseTargetRegistry::find)
                            .orElse(UNRESOLVED_CASE_TARGET)
            );
        }
        Map<String, List<ContractScenario>> grouped = new LinkedHashMap<>();
        for (ContractScenario scenario : scenarios) {
            grouped.computeIfAbsent(targetByScenarioId.getOrDefault(scenario.uniqueId(), UNRESOLVED_CASE_TARGET), key -> new ArrayList<>())
                    .add(scenario);
        }
        return grouped;
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
