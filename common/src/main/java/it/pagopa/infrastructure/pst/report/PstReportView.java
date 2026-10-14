package it.pagopa.infrastructure.pst.report;

import it.pagopa.infrastructure.contract.http.RequestScope;
import it.pagopa.infrastructure.pst.model.PstDocument;
import it.pagopa.infrastructure.pst.model.PstOperation;
import it.pagopa.infrastructure.pst.model.PstScenario;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Read-only projection of a {@link PstDocument}: it only groups and counts values already present in the model.
 */
public record PstReportView(
        String title,
        int operationCount,
        int scenarioCount,
        List<OperationView> operations
) {

    /**
     * Scopes actually designed, in declaration order: feeds the report scope filter without
     * exposing options that would match nothing.
     */
    public List<String> scopes() {
        List<String> scopes = new ArrayList<>();
        for (OperationView operation : operations) {
            for (ScopeView scope : operation.scopes()) {
                if (!scopes.contains(scope.scope())) {
                    scopes.add(scope.scope());
                }
            }
        }
        return List.copyOf(scopes);
    }

    public static PstReportView from(String title, PstDocument document) {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(document, "document must not be null");
        List<OperationView> operations = new ArrayList<>();
        int scenarioCount = 0;
        for (int index = 0; index < document.operations().size(); index++) {
            PstOperation operation = document.operations().get(index);
            scenarioCount += operation.scenarios().size();
            operations.add(OperationView.from("operation-" + index, operation));
        }
        return new PstReportView(title, document.operations().size(), scenarioCount, List.copyOf(operations));
    }

    public record OperationView(
            String anchor,
            String operationId,
            String httpMethod,
            String path,
            int scenarioCount,
            List<ScopeView> scopes
    ) {
        static OperationView from(String anchor, PstOperation operation) {
            Map<RequestScope, List<ScenarioView>> byScope = new LinkedHashMap<>();
            for (PstScenario scenario : operation.scenarios()) {
                byScope.computeIfAbsent(scenario.scope(), ignored -> new ArrayList<>()).add(ScenarioView.from(scenario));
            }
            List<ScopeView> scopes = byScope.entrySet().stream()
                    .map(entry -> new ScopeView(entry.getKey().name(), entry.getValue().size(), List.copyOf(entry.getValue())))
                    .toList();
            return new OperationView(
                    anchor,
                    operation.operationId(),
                    operation.httpMethod(),
                    operation.path(),
                    operation.scenarios().size(),
                    scopes
            );
        }
    }

    public record ScopeView(String scope, int scenarioCount, List<ScenarioView> scenarios) {
    }

    /**
     * {@code statusClass} depends only on the status code family; {@code details} exposes validity and
     * expectation origin as a tooltip, without dedicated columns.
     */
    public record ScenarioView(
            String scope,
            String target,
            String scenario,
            int expectedStatus,
            String statusClass,
            String details
    ) {
        static ScenarioView from(PstScenario scenario) {
            return new ScenarioView(
                    scenario.scope().name(),
                    PstTargetFormatter.format(scenario.scope(), scenario.target()),
                    scenario.scenario().name(),
                    scenario.expectedStatus(),
                    statusClass(scenario.expectedStatus()),
                    scenario.validity().name() + " · " + scenario.expectationOrigin().name()
            );
        }

        private static String statusClass(int status) {
            int family = status / 100;
            return family >= 2 && family <= 5 ? "status-" + family + "xx" : "status-other";
        }
    }
}
