package it.pagopa.interop.common.infrastructure.objectgraph;

import java.util.Objects;

public final class NodeSelectors {

    private NodeSelectors() {
    }

    public static NodeSelector all() {
        return node -> true;
    }

    public static NodeSelector byKind(NodeKind kind) {
        Objects.requireNonNull(kind, "kind must not be null");
        return node -> node.kind() == kind;
    }

    public static NodeSelector scalar() {
        return byKind(NodeKind.SCALAR);
    }

    public static NodeSelector object() {
        return byKind(NodeKind.OBJECT);
    }

    public static NodeSelector collection() {
        return byKind(NodeKind.COLLECTION);
    }

    public static NodeSelector leaf() {
        return Node::isLeaf;
    }

    public static NodeSelector root() {
        return Node::isRoot;
    }

    public static NodeSelector valueType(Class<?> type) {
        Objects.requireNonNull(type, "type must not be null");
        return node -> node.value() != null && type.isInstance(node.value());
    }
}
