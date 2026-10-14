package it.pagopa.infrastructure.fuzzing;

import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FuzzCasePlanner {

    private final List<FuzzRule> rules;

    public FuzzCasePlanner(List<FuzzRule> rules) {
        this.rules = List.copyOf(Objects.requireNonNull(rules, "rules must not be null"));
    }

    public List<PlannedFuzzCase> plan(ObjectGraph graph) {
        Objects.requireNonNull(graph, "graph must not be null");
        List<PlannedFuzzCase> plannedCases = new ArrayList<>();

        for (FuzzRule rule : rules) {
            for (Node node : graph.select(rule.selector())) {
                for (FuzzMutation mutation : rule.mutationsFor(node, graph)) {
                    plannedCases.add(new PlannedFuzzCase(node.path(), mutation));
                }
            }
        }

        return plannedCases;
    }
}
