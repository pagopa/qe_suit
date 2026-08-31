package it.pagopa.interop.common.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;

public record FuzzCase(
        NodePath target,
        FuzzMutation mutation,
        JsonNode result
) {
    public FuzzCase {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
        if (mutation == null) {
            throw new IllegalArgumentException("mutation must not be null");
        }
    }
}
