package it.pagopa.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

final class ScopeOverrides {
    private final EnumMap<FuzzScenario, Consumer<Response>> scenarioOverrides = new EnumMap<>(FuzzScenario.class);
    private final List<TargetOverride> targetOverrides = new ArrayList<>();

    void addScenario(List<FuzzScenario> scenarios, Consumer<Response> expectation) {
        validateScenarios(scenarios);
        Objects.requireNonNull(expectation, "expectation must not be null");
        for (FuzzScenario scenario : scenarios) {
            if (scenarioOverrides.putIfAbsent(scenario, expectation) != null) {
                throw new ContractHttpException("Duplicate scenario override: " + scenario);
            }
        }
    }

    void addTargets(List<FuzzScenario> scenarios, Consumer<Response> expectation, List<? extends TargetExpression<?>> targets) {
        validateScenarios(scenarios);
        Objects.requireNonNull(expectation, "expectation must not be null");
        if (targets == null || targets.isEmpty()) {
            throw new ContractHttpException("targets must not be empty");
        }
        Set<TargetExpression<?>> seen = new HashSet<>();
        for (TargetExpression<?> target : targets) {
            if (target == null) throw new ContractHttpException("target expression must not be null");
            if (!seen.add(target)) throw new ContractHttpException("Duplicate target expression in same override");
        }
        targetOverrides.add(new TargetOverride(List.copyOf(scenarios), expectation, List.copyOf(targets)));
    }

    Consumer<Response> scenario(FuzzScenario scenario) {
        return scenarioOverrides.get(scenario);
    }

    List<TargetOverride> targets() {
        return List.copyOf(targetOverrides);
    }

    private void validateScenarios(List<FuzzScenario> scenarios) {
        if (scenarios == null) throw new ContractHttpException("scenarios must not be null");
        if (scenarios.isEmpty()) throw new ContractHttpException("scenarios must not be empty");
        if (scenarios.stream().anyMatch(Objects::isNull)) {
            throw new ContractHttpException("scenarios must not contain null elements");
        }
    }

    record TargetOverride(
            List<FuzzScenario> scenarios,
            Consumer<Response> expectation,
            List<TargetExpression<?>> targets
    ) {
    }
}
