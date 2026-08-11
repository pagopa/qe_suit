package it.pagopa.interop.common.infrastructure.fuzzing;

import it.pagopa.interop.common.infrastructure.objectgraph.Node;
import it.pagopa.interop.common.infrastructure.objectgraph.NodeSelector;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;

import java.util.List;

interface FuzzRule {

    NodeSelector selector();

    List<FuzzMutation> mutationsFor(Node node, ObjectGraph graph);
}
