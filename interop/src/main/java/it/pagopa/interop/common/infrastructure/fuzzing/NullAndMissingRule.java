package it.pagopa.interop.common.infrastructure.fuzzing;

import it.pagopa.interop.common.infrastructure.objectgraph.Node;
import it.pagopa.interop.common.infrastructure.objectgraph.NodeSelector;
import it.pagopa.interop.common.infrastructure.objectgraph.NodeSelectors;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class NullAndMissingRule implements FuzzRule {

    private static final List<FuzzMutation> MUTATIONS = List.of(
            new FuzzMutation(FuzzScenario.REPLACED_WITH_NULL, FuzzMutationKind.REPLACE, null),
            new FuzzMutation(FuzzScenario.REMOVED, FuzzMutationKind.REMOVE, null)
    );

    @Override
    public NodeSelector selector() {
        return NodeSelectors.all();
    }

    @Override
    public List<FuzzMutation> mutationsFor(Node node, ObjectGraph graph) {
        return MUTATIONS;
    }
}
