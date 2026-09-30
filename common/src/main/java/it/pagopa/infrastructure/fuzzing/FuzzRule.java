package it.pagopa.infrastructure.fuzzing;

import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.NodeSelector;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;

import java.util.List;

public interface FuzzRule {

    NodeSelector selector();

    List<FuzzMutation> mutationsFor(Node node, ObjectGraph graph);
}
