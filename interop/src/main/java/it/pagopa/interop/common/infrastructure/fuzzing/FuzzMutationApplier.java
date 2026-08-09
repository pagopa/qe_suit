package it.pagopa.interop.common.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;

interface FuzzMutationApplier {
    JsonNode apply(JsonNode target, NodePath path, FuzzMutation mutation);
}
