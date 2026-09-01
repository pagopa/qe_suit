package it.pagopa.infrastructure.fuzzing;

import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.NodeSelector;
import it.pagopa.infrastructure.objectgraph.NodeSelectors;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Component
class ScalarRule implements FuzzRule {

    private static final Logger logger = LoggerFactory.getLogger(ScalarRule.class);

    @Override
    public NodeSelector selector() {
        return NodeSelectors.scalar();
    }

    @Override
    public List<FuzzMutation> mutationsFor(Node node, ObjectGraph graph) {
        Class<?> type = node.javaType();

        if (type == String.class) return stringMutations();
        if (type == byte.class || type == Byte.class) return integerMutations((byte) 0, (byte) -1, Byte.MIN_VALUE, Byte.MAX_VALUE);
        if (type == short.class || type == Short.class)
            return integerMutations((short) 0, (short) -1, Short.MIN_VALUE, Short.MAX_VALUE);
        if (type == int.class || type == Integer.class) return integerMutations(0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (type == long.class || type == Long.class) return integerMutations(0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE);
        if (type == BigInteger.class) return bigIntegerMutations();
        if (type == float.class || type == Float.class) return floatingMutations(0.0f, -1.0f, -Float.MAX_VALUE, Float.MAX_VALUE);
        if (type == double.class || type == Double.class)
            return floatingMutations(0.0d, -1.0d, -Double.MAX_VALUE, Double.MAX_VALUE);
        if (type == BigDecimal.class) return bigDecimalMutations();
        if (type == boolean.class || type == Boolean.class) return booleanMutations();
        if (type == UUID.class) return uuidMutations();
        if (type.isEnum()) return enumMutations();

        logger.warn("Unsupported scalar type for fuzzing at path {}: {}", node.path(), type.getName());
        return List.of();
    }

    private List<FuzzMutation> stringMutations() {
        return List.of(
                replace(FuzzScenario.REPLACED_WITH_EMPTY_STRING, ""),
                replace(FuzzScenario.REPLACED_WITH_BLANK_STRING, "   "),
                replace(FuzzScenario.REPLACED_WITH_LONG_STRING, "A".repeat(5000)),
                replace(FuzzScenario.REPLACED_WITH_SQL_INJECTION, "' OR '1'='1"),
                replace(FuzzScenario.REPLACED_WITH_XSS, "<script>alert(1)</script>"),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER, 124)
        );
    }

    private List<FuzzMutation> integerMutations(Object zero, Object negative, Object min, Object max) {
        return List.of(
                replace(FuzzScenario.REPLACED_WITH_ZERO, zero),
                replace(FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE, negative),
                replace(FuzzScenario.REPLACED_WITH_MIN_VALUE, min),
                replace(FuzzScenario.REPLACED_WITH_MAX_VALUE, max),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING, "not-a-number"),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_DECIMAL, 124.23)
        );
    }

    private List<FuzzMutation> bigIntegerMutations() {
        return List.of(
                replace(FuzzScenario.REPLACED_WITH_ZERO, BigInteger.ZERO),
                replace(FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE, BigInteger.valueOf(-1)),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING, "not-a-number"),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_DECIMAL, 124.23)
        );
    }

    private List<FuzzMutation> floatingMutations(Object zero, Object negative, Object min, Object max) {
        return List.of(
                replace(FuzzScenario.REPLACED_WITH_ZERO, zero),
                replace(FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE, negative),
                replace(FuzzScenario.REPLACED_WITH_MIN_VALUE, min),
                replace(FuzzScenario.REPLACED_WITH_MAX_VALUE, max),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING, "not-a-number")
        );
    }

    private List<FuzzMutation> bigDecimalMutations() {
        return List.of(
                replace(FuzzScenario.REPLACED_WITH_ZERO, BigDecimal.ZERO),
                replace(FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE, BigDecimal.valueOf(-1)),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING, "not-a-number")
        );
    }

    private List<FuzzMutation> booleanMutations() {
        return List.of(
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING, "not-a-boolean"),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER, 1)
        );
    }

    private List<FuzzMutation> uuidMutations() {
        return List.of(
                replace(FuzzScenario.REPLACED_WITH_MALFORMED_UUID, "not-a-valid-uuid-12345"),
                replace(FuzzScenario.REPLACED_WITH_NIL_UUID, "00000000-0000-0000-0000-000000000000"),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER, 123)
        );
    }

    private List<FuzzMutation> enumMutations() {
        return List.of(
                replace(FuzzScenario.REPLACED_WITH_UNKNOWN_ENUM, "__UNKNOWN__"),
                replace(FuzzScenario.REPLACED_WITH_EMPTY_STRING, ""),
                replace(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER, 123)
        );
    }

    private FuzzMutation replace(FuzzScenario scenario, Object value) {
        return new FuzzMutation(scenario, FuzzMutationKind.REPLACE, value);
    }
}
