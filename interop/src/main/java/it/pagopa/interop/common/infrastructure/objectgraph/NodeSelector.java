package it.pagopa.interop.common.infrastructure.objectgraph;

import java.util.Objects;

@FunctionalInterface
public interface NodeSelector {

    boolean matches(Node node);

    default NodeSelector and(NodeSelector other) {
        Objects.requireNonNull(other, "other must not be null");
        return node -> this.matches(node) && other.matches(node);
    }

    default NodeSelector or(NodeSelector other) {
        Objects.requireNonNull(other, "other must not be null");
        return node -> this.matches(node) || other.matches(node);
    }

    default NodeSelector negate() {
        return node -> !this.matches(node);
    }
}
