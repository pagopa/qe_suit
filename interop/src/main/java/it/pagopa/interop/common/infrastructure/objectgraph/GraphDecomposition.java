package it.pagopa.interop.common.infrastructure.objectgraph;

import java.util.List;
import java.util.Map;

record GraphDecomposition(List<Node> nodes, Map<QueryEdge, NodePath> queryEdges) {
}
