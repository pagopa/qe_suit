package it.pagopa.infrastructure.objectgraph;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ObjectGraph {

    private final List<Node> nodes;
    private final Map<NodePath, Node> byPath;
    private final Map<QueryEdge, NodePath> queryEdges;

    ObjectGraph(List<Node> nodes) {
        this(nodes, Map.of());
    }

    ObjectGraph(List<Node> nodes, Map<QueryEdge, NodePath> queryEdges) {
        Objects.requireNonNull(nodes, "nodes must not be null");
        if (nodes.isEmpty()) {
            throw new ObjectGraphException("ObjectGraph requires at least a root node");
        }
        this.nodes = List.copyOf(nodes);
        this.byPath = this.nodes.stream().collect(Collectors.toUnmodifiableMap(Node::path, Function.identity()));
        this.queryEdges = Map.copyOf(Objects.requireNonNull(queryEdges, "queryEdges must not be null"));
    }

    public List<Node> nodes() {
        return nodes;
    }

    public Node root() {
        return find(NodePath.root())
                .orElseThrow(() -> new ObjectGraphException("ObjectGraph does not contain root node"));
    }

    public Optional<Node> find(NodePath path) {
        Objects.requireNonNull(path, "path must not be null");
        return Optional.ofNullable(byPath.get(path));
    }

    public Node find(ObjectGraphQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        Node current = root();
        for (QueryStep step : query.steps()) {
            QueryEdge edge = new QueryEdge(current.path(), step);
            NodePath childPath = queryEdges.get(edge);
            if (childPath == null) {
                throw new ObjectGraphException("ObjectGraphQuery is not resolvable from path '" + current.path() + "'");
            }
            current = Optional.ofNullable(byPath.get(childPath))
                    .orElseThrow(() -> new ObjectGraphException("ObjectGraph is inconsistent for path '" + childPath + "'"));
        }
        return current;
    }

    public List<Node> childrenOf(Node node) {
        Objects.requireNonNull(node, "node must not be null");
        return nodes.stream()
                .filter(candidate -> candidate.path().isDirectChildOf(node.path()))
                .toList();
    }

    public List<Node> select(NodeSelector selector) {
        Objects.requireNonNull(selector, "selector must not be null");
        return nodes.stream()
                .filter(selector::matches)
                .toList();
    }
}
