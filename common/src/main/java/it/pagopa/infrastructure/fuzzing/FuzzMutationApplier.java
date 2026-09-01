package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.infrastructure.objectgraph.NodePath;

public interface FuzzMutationApplier {
    JsonNode apply(JsonNode target, NodePath path, FuzzMutation mutation);
}
