package it.pagopa.interop.common.infrastructure.objectgraph;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ObjectGraph {

    private final List<Node> nodes;

    ObjectGraph(List<Node> nodes) {
        Objects.requireNonNull(nodes, "nodes must not be null");
        if (nodes.isEmpty()) {
            throw new ObjectGraphException("ObjectGraph requires at least a root node");
        }
        this.nodes = List.copyOf(nodes);
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
        return nodes.stream()
                .filter(node -> node.path().equals(path))
                .findFirst();
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
