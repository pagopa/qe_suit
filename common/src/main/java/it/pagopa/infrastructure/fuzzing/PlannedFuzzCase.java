package it.pagopa.infrastructure.fuzzing;

import it.pagopa.infrastructure.objectgraph.NodePath;

public record PlannedFuzzCase(
        NodePath target,
        FuzzMutation mutation
) {
    public PlannedFuzzCase {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
        if (mutation == null) {
            throw new IllegalArgumentException("mutation must not be null");
        }
    }
}
