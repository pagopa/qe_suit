package it.pagopa.interop.common.infrastructure.fuzzing;

import it.pagopa.interop.common.infrastructure.objectgraph.Node;
import it.pagopa.interop.common.infrastructure.objectgraph.NodeKind;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ScalarRuleTest {

    private final ScalarRule rule = new ScalarRule();
    private final ObjectGraph graph = mock(ObjectGraph.class);

    @Test
    void string_mutations_are_generated_with_exact_values() {
        Map<FuzzScenario, FuzzMutation> byScenario = byScenarioForType(String.class);
        assertEquals("", byScenario.get(FuzzScenario.REPLACED_WITH_EMPTY_STRING).value());
        assertEquals("   ", byScenario.get(FuzzScenario.REPLACED_WITH_BLANK_STRING).value());
        assertEquals("A".repeat(5000), byScenario.get(FuzzScenario.REPLACED_WITH_LONG_STRING).value());
        assertEquals("' OR '1'='1", byScenario.get(FuzzScenario.REPLACED_WITH_SQL_INJECTION).value());
        assertEquals("<script>alert(1)</script>", byScenario.get(FuzzScenario.REPLACED_WITH_XSS).value());
        assertEquals(124, byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER).value());
    }

    @Test
    void integral_numeric_families_cover_primitive_and_wrapper_variants() {
        assertIntegralFamily(byte.class, (byte) 0, (byte) -1, Byte.MIN_VALUE, Byte.MAX_VALUE);
        assertIntegralFamily(Byte.class, (byte) 0, (byte) -1, Byte.MIN_VALUE, Byte.MAX_VALUE);
        assertIntegralFamily(short.class, (short) 0, (short) -1, Short.MIN_VALUE, Short.MAX_VALUE);
        assertIntegralFamily(Short.class, (short) 0, (short) -1, Short.MIN_VALUE, Short.MAX_VALUE);
        assertIntegralFamily(int.class, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertIntegralFamily(Integer.class, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertIntegralFamily(long.class, 0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE);
        assertIntegralFamily(Long.class, 0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Test
    void big_integer_has_expected_subset_without_min_or_max() {
        Map<FuzzScenario, FuzzMutation> byScenario = byScenarioForType(BigInteger.class);
        assertEquals(BigInteger.ZERO, byScenario.get(FuzzScenario.REPLACED_WITH_ZERO).value());
        assertEquals(BigInteger.valueOf(-1), byScenario.get(FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE).value());
        assertEquals("not-a-number", byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING).value());
        assertEquals(124.23, byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_DECIMAL).value());
        assertFalse(byScenario.containsKey(FuzzScenario.REPLACED_WITH_MIN_VALUE));
        assertFalse(byScenario.containsKey(FuzzScenario.REPLACED_WITH_MAX_VALUE));
    }

    @Test
    void float_and_double_use_negative_max_as_min_extreme() {
        Map<FuzzScenario, FuzzMutation> floatMutations = byScenarioForType(float.class);
        assertEquals(0.0f, floatMutations.get(FuzzScenario.REPLACED_WITH_ZERO).value());
        assertEquals(-1.0f, floatMutations.get(FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE).value());
        assertEquals(-Float.MAX_VALUE, floatMutations.get(FuzzScenario.REPLACED_WITH_MIN_VALUE).value());
        assertEquals(Float.MAX_VALUE, floatMutations.get(FuzzScenario.REPLACED_WITH_MAX_VALUE).value());
        assertEquals("not-a-number", floatMutations.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING).value());
        assertTrue(!Float.valueOf(Float.MIN_VALUE).equals(floatMutations.get(FuzzScenario.REPLACED_WITH_MIN_VALUE).value()));

        Map<FuzzScenario, FuzzMutation> doubleMutations = byScenarioForType(Double.class);
        assertEquals(0.0d, doubleMutations.get(FuzzScenario.REPLACED_WITH_ZERO).value());
        assertEquals(-1.0d, doubleMutations.get(FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE).value());
        assertEquals(-Double.MAX_VALUE, doubleMutations.get(FuzzScenario.REPLACED_WITH_MIN_VALUE).value());
        assertEquals(Double.MAX_VALUE, doubleMutations.get(FuzzScenario.REPLACED_WITH_MAX_VALUE).value());
        assertEquals("not-a-number", doubleMutations.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING).value());
        assertTrue(!Double.valueOf(Double.MIN_VALUE).equals(doubleMutations.get(FuzzScenario.REPLACED_WITH_MIN_VALUE).value()));
    }

    @Test
    void big_decimal_has_expected_subset_without_min_or_max() {
        Map<FuzzScenario, FuzzMutation> byScenario = byScenarioForType(BigDecimal.class);
        assertEquals(BigDecimal.ZERO, byScenario.get(FuzzScenario.REPLACED_WITH_ZERO).value());
        assertEquals(BigDecimal.valueOf(-1), byScenario.get(FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE).value());
        assertEquals("not-a-number", byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING).value());
        assertFalse(byScenario.containsKey(FuzzScenario.REPLACED_WITH_MIN_VALUE));
        assertFalse(byScenario.containsKey(FuzzScenario.REPLACED_WITH_MAX_VALUE));
    }

    @Test
    void boolean_family_covers_primitive_and_wrapper() {
        assertBooleanFamily(boolean.class);
        assertBooleanFamily(Boolean.class);
    }

    @Test
    void uuid_mutations_have_exact_values() {
        Map<FuzzScenario, FuzzMutation> byScenario = byScenarioForType(UUID.class);
        assertEquals("not-a-valid-uuid-12345", byScenario.get(FuzzScenario.REPLACED_WITH_MALFORMED_UUID).value());
        assertEquals("00000000-0000-0000-0000-000000000000", byScenario.get(FuzzScenario.REPLACED_WITH_NIL_UUID).value());
        assertEquals(123, byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER).value());
    }

    @Test
    void enum_mutations_are_generated() {
        Map<FuzzScenario, FuzzMutation> byScenario = byScenarioForType(Status.class);
        assertEquals("__UNKNOWN__", byScenario.get(FuzzScenario.REPLACED_WITH_UNKNOWN_ENUM).value());
        assertEquals("", byScenario.get(FuzzScenario.REPLACED_WITH_EMPTY_STRING).value());
        assertEquals(123, byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER).value());
    }

    @Test
    void unsupported_scalar_returns_empty_list() {
        Node node = new Node(NodeKind.SCALAR, NodePath.root(), 'a', Character.class);
        List<FuzzMutation> mutations = rule.mutationsFor(node, graph);
        assertTrue(mutations.isEmpty());
    }

    private void assertIntegralFamily(Class<?> type, Object zero, Object negative, Object min, Object max) {
        Map<FuzzScenario, FuzzMutation> byScenario = byScenarioForType(type);
        assertEquals(zero, byScenario.get(FuzzScenario.REPLACED_WITH_ZERO).value());
        assertEquals(negative, byScenario.get(FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE).value());
        assertEquals(min, byScenario.get(FuzzScenario.REPLACED_WITH_MIN_VALUE).value());
        assertEquals(max, byScenario.get(FuzzScenario.REPLACED_WITH_MAX_VALUE).value());
        assertEquals("not-a-number", byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING).value());
        assertEquals(124.23, byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_DECIMAL).value());
    }

    private void assertBooleanFamily(Class<?> type) {
        Map<FuzzScenario, FuzzMutation> byScenario = byScenarioForType(type);
        assertEquals("not-a-boolean", byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING).value());
        assertEquals(1, byScenario.get(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER).value());
    }

    private Map<FuzzScenario, FuzzMutation> byScenarioForType(Class<?> type) {
        Node node = new Node(NodeKind.SCALAR, NodePath.root(), null, type);
        return rule.mutationsFor(node, graph).stream().collect(Collectors.toMap(FuzzMutation::scenario, Function.identity()));
    }

    enum Status {
        ACTIVE
    }
}
