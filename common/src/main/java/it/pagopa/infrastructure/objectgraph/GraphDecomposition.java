package it.pagopa.infrastructure.objectgraph;

import java.util.List;
import java.util.Map;

record GraphDecomposition(List<Node> nodes, Map<QueryEdge, NodePath> queryEdges) {
}
