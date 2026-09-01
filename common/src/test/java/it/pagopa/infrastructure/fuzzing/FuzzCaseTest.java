package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import it.pagopa.infrastructure.objectgraph.NodePath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuzzCaseTest {

    private static final FuzzMutation MUTATION = new FuzzMutation(FuzzScenario.REPLACED_WITH_NULL, FuzzMutationKind.REPLACE, null);

    @Test
    void rejects_null_target() {
        assertThrows(IllegalArgumentException.class, () -> new FuzzCase(null, MUTATION, JsonNodeFactory.instance.objectNode()));
    }

    @Test
    void rejects_null_mutation() {
        assertThrows(IllegalArgumentException.class, () -> new FuzzCase(NodePath.root(), null, JsonNodeFactory.instance.objectNode()));
    }

    @Test
    void accepts_java_null_result() {
        FuzzCase fuzzCase = assertDoesNotThrow(() -> new FuzzCase(NodePath.root(), MUTATION, null));
        assertNull(fuzzCase.result());
    }

    @Test
    void accepts_jackson_nullnode_result_as_distinct_from_java_null() {
        FuzzCase fuzzCase = assertDoesNotThrow(() -> new FuzzCase(NodePath.root(), MUTATION, NullNode.getInstance()));
        assertNotNull(fuzzCase.result());
        assertTrue(fuzzCase.result().isNull());
    }
}
