package it.pagopa.interop.common.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

final class JacksonCollectionCorrelation {

    private final ObjectMapper objectMapper;

    JacksonCollectionCorrelation(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    void correlate(ArrayNode arrayNode, Object javaValue, JavaType declaredType, NodePath path, List<Node> nodes, NodeWalker walker) {
        if (javaValue == null) return;

        JavaType itemType = declaredType.getContentType();
        if (javaValue.getClass().isArray()) {
            correlateArray(arrayNode, javaValue, itemType, path, nodes, walker);
            return;
        }
        if (javaValue instanceof Iterable<?> iterable) {
            correlateIterable(arrayNode, iterable, itemType, path, declaredType, nodes, walker);
            return;
        }
        throw ObjectGraphErrors.fail("Java value is not an array or iterable", path, declaredType, null);
    }

    private void correlateArray(ArrayNode arrayNode, Object arrayValue, JavaType itemType, NodePath path, List<Node> nodes, NodeWalker walker) {
        int javaSize = Array.getLength(arrayValue);
        if (javaSize != arrayNode.size()) {
            throw ObjectGraphErrors.fail("Array and JSON array cardinality mismatch", path, null, null);
        }
        for (int index = 0; index < arrayNode.size(); index++) {
            Object childValue = Array.get(arrayValue, index);
            JavaType childType = itemType != null ? itemType : resolveFallbackType(childValue);
            walker.visit(arrayNode.get(index), childValue, childType, path.index(index), nodes);
        }
    }

    private void correlateIterable(ArrayNode arrayNode, Iterable<?> iterable, JavaType itemType, NodePath path, JavaType declaredType, List<Node> nodes, NodeWalker walker) {
        Iterator<?> iterator = iterable.iterator();
        for (int index = 0; index < arrayNode.size(); index++) {
            if (!iterator.hasNext()) {
                throw ObjectGraphErrors.fail("Collection and JSON array cardinality mismatch", path, declaredType, null);
            }
            Object childValue = iterator.next();
            JavaType childType = itemType != null ? itemType : resolveFallbackType(childValue);
            walker.visit(arrayNode.get(index), childValue, childType, path.index(index), nodes);
        }
        if (iterator.hasNext()) {
            throw ObjectGraphErrors.fail("Collection and JSON array cardinality mismatch", path, declaredType, null);
        }
    }

    private JavaType resolveFallbackType(Object value) {
        return value == null ? null : objectMapper.constructType(value.getClass());
    }
}
