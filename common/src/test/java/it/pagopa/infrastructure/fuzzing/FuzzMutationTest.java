package it.pagopa.infrastructure.fuzzing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FuzzMutationTest {

    @Test
    void rejects_null_scenario() {
        assertThrows(IllegalArgumentException.class, () -> new FuzzMutation(null, FuzzMutationKind.REPLACE, "x"));
    }

    @Test
    void rejects_null_kind() {
        assertThrows(IllegalArgumentException.class, () -> new FuzzMutation(FuzzScenario.REPLACED_WITH_NULL, null, "x"));
    }

    @Test
    void accepts_remove_with_null_value() {
        assertDoesNotThrow(() -> new FuzzMutation(FuzzScenario.REMOVED, FuzzMutationKind.REMOVE, null));
    }

    @Test
    void rejects_remove_with_non_null_value() {
        assertThrows(IllegalArgumentException.class, () -> new FuzzMutation(FuzzScenario.REMOVED, FuzzMutationKind.REMOVE, "x"));
    }

    @Test
    void accepts_replace_with_null_value() {
        assertDoesNotThrow(() -> new FuzzMutation(FuzzScenario.REPLACED_WITH_NULL, FuzzMutationKind.REPLACE, null));
    }

    @Test
    void accepts_replace_with_non_null_value() {
        assertDoesNotThrow(() -> new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, ""));
    }
}
