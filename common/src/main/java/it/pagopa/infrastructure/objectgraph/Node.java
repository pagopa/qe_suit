package it.pagopa.infrastructure.objectgraph;

public record Node(
        NodeKind kind,
        NodePath path,
        Object value,
        Class<?> javaType
) {
    public Node {
        if (kind == null) {
            throw new IllegalArgumentException("kind must not be null");
        }
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        if (javaType == null) {
            throw new IllegalArgumentException("javaType must not be null");
        }
    }

    public boolean isRoot() {
        return path.isRoot();
    }

    public boolean isLeaf() {
        return kind == NodeKind.SCALAR;
    }
}
