package it.pagopa.infrastructure.objectgraph;

import java.util.Objects;

record MapKeyStep(String key) implements QueryStep {
    MapKeyStep {
        Objects.requireNonNull(key, "key must not be null");
    }
}
