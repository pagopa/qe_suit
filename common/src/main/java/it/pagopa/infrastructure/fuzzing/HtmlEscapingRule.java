package it.pagopa.infrastructure.fuzzing;

import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.NodeSelector;
import it.pagopa.infrastructure.objectgraph.NodeSelectors;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;

import java.util.List;

public class HtmlEscapingRule implements FuzzRule {

    @Override
    public NodeSelector selector() {
        return NodeSelectors.scalar();
    }

    @Override
    public List<FuzzMutation> mutationsFor(Node node, ObjectGraph graph) {
        if (node.javaType() != String.class) {
            return List.of();
        }

        return List.of(
                replace(FuzzScenario.REPLACED_WITH_LESS_THAN, "<"),
                replace(FuzzScenario.REPLACED_WITH_GREATER_THAN, ">"),
                replace(FuzzScenario.REPLACED_WITH_AMPERSAND, "&"),
                replace(FuzzScenario.REPLACED_WITH_DOUBLE_QUOTE, "\""),
                replace(FuzzScenario.REPLACED_WITH_SINGLE_QUOTE, "'")
        );
    }

    private FuzzMutation replace(FuzzScenario scenario, String value) {
        return new FuzzMutation(scenario, FuzzMutationKind.REPLACE, value);
    }
}
