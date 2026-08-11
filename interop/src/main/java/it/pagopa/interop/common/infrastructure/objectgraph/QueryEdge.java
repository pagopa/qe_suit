package it.pagopa.interop.common.infrastructure.objectgraph;

import java.util.Objects;

record QueryEdge(NodePath parentPath, QueryStep step) {
    QueryEdge {
        Objects.requireNonNull(parentPath, "parentPath must not be null");
        Objects.requireNonNull(step, "step must not be null");
    }
}
