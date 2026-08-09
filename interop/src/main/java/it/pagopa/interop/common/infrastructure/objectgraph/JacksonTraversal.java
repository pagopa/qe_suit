package it.pagopa.interop.common.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class JacksonTraversal {

    private final ObjectMapper objectMapper;

    JacksonTraversal(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    List<Node> decompose(JsonNode rootJson, Object source, JavaType rootType) {
        List<Node> nodes = new ArrayList<>();
        visit(rootJson, source, rootType, NodePath.root(), nodes);
        return nodes;
    }

    private void visit(JsonNode jsonNode, Object javaValue, JavaType declaredType, NodePath path, List<Node> nodes) {
        NodeKind kind = resolveNodeKind(jsonNode, declaredType, path);
        nodes.add(new Node(kind, path, javaValue, resolveJavaClass(declaredType, javaValue)));
        if (jsonNode.isNull() || kind == NodeKind.SCALAR) return;
        if (kind == NodeKind.COLLECTION) {
            visitCollection(jsonNode, javaValue, declaredType, path, nodes);
        } else {
            visitObject(jsonNode, javaValue, declaredType, path, nodes);
        }
    }

    private void visitObject(JsonNode jsonNode, Object javaValue, JavaType declaredType, NodePath path, List<Node> nodes) {
        if (!(jsonNode instanceof ObjectNode objectNode)) throw fail("Expected JSON object", path, declaredType, null);
        if (javaValue == null) return;
        if (javaValue instanceof Map<?, ?> map) {
            visitMap(objectNode, map, declaredType, path, nodes);
            return;
        }

        BeanDescription description = introspect(declaredType);
        List<BeanPropertyDefinition> properties = description.findProperties();
        int visitedChildren = 0;
        for (BeanPropertyDefinition property : properties) {
            AnnotatedMember accessor = property.getAccessor();
            if (accessor == null) throw fail("Property accessor not available for '" + property.getName() + "'", path, declaredType, null);

            String jsonName = property.getName();
            JsonNode childJson = objectNode.get(jsonName);
            if (childJson == null) throw fail("Cannot correlate property '" + jsonName + "' with JSON object field", path, declaredType, null);

            Object childValue = readPropertyValue(accessor, javaValue, jsonName, path, declaredType);
            visit(childJson, childValue, accessor.getType(), path.property(jsonName), nodes);
            visitedChildren++;
        }
        if (visitedChildren != objectNode.size()) throw fail("JSON object contains fields that cannot be correlated", path, declaredType, null);
    }

    private void visitMap(ObjectNode objectNode, Map<?, ?> map, JavaType declaredType, NodePath path, List<Node> nodes) {
        if (map.size() != objectNode.size()) throw fail("Map and JSON object cardinality mismatch", path, declaredType, null);
        Iterator<? extends Map.Entry<?, ?>> mapIterator = map.entrySet().iterator();
        Iterator<Map.Entry<String, JsonNode>> jsonIterator = objectNode.fields();
        JavaType valueType = declaredType.getContentType();

        while (mapIterator.hasNext() && jsonIterator.hasNext()) {
            Object childValue = mapIterator.next().getValue();
            Map.Entry<String, JsonNode> jsonEntry = jsonIterator.next();
            JavaType childType = valueType != null ? valueType : resolveFallbackType(childValue);
            visit(jsonEntry.getValue(), childValue, childType, path.property(jsonEntry.getKey()), nodes);
        }
        if (mapIterator.hasNext() || jsonIterator.hasNext()) throw fail("Map and JSON object cannot be correlated", path, declaredType, null);
    }

    private void visitCollection(JsonNode jsonNode, Object javaValue, JavaType declaredType, NodePath path, List<Node> nodes) {
        if (!(jsonNode instanceof ArrayNode arrayNode)) throw fail("Expected JSON array", path, declaredType, null);
        if (javaValue == null) return;

        JavaType itemType = declaredType.getContentType();
        if (javaValue.getClass().isArray()) {
            visitArray(arrayNode, javaValue, itemType, path, nodes);
        } else if (javaValue instanceof Iterable<?> iterable) {
            visitIterable(arrayNode, iterable, itemType, path, declaredType, nodes);
        } else {
            throw fail("Java value is not an array or iterable", path, declaredType, null);
        }
    }

    private void visitArray(ArrayNode arrayNode, Object arrayValue, JavaType itemType, NodePath path, List<Node> nodes) {
        int javaSize = Array.getLength(arrayValue);
        if (javaSize != arrayNode.size()) throw fail("Array and JSON array cardinality mismatch", path, null, null);
        for (int index = 0; index < arrayNode.size(); index++) {
            Object childValue = Array.get(arrayValue, index);
            JavaType childType = itemType != null ? itemType : resolveFallbackType(childValue);
            visit(arrayNode.get(index), childValue, childType, path.index(index), nodes);
        }
    }

    private void visitIterable(ArrayNode arrayNode, Iterable<?> iterable, JavaType itemType, NodePath path, JavaType declaredType, List<Node> nodes) {
        Iterator<?> iterator = iterable.iterator();
        for (int index = 0; index < arrayNode.size(); index++) {
            if (!iterator.hasNext()) throw fail("Collection and JSON array cardinality mismatch", path, declaredType, null);
            Object childValue = iterator.next();
            JavaType childType = itemType != null ? itemType : resolveFallbackType(childValue);
            visit(arrayNode.get(index), childValue, childType, path.index(index), nodes);
        }
        if (iterator.hasNext()) throw fail("Collection and JSON array cardinality mismatch", path, declaredType, null);
    }

    private NodeKind resolveNodeKind(JsonNode jsonNode, JavaType declaredType, NodePath path) {
        if (jsonNode.isObject()) return NodeKind.OBJECT;
        if (jsonNode.isArray()) return NodeKind.COLLECTION;
        if (!jsonNode.isNull()) return NodeKind.SCALAR;
        return inferNullKind(declaredType, path);
    }

    private NodeKind inferNullKind(JavaType declaredType, NodePath path) {
        if (declaredType == null) throw new ObjectGraphException("Cannot infer null node kind without declared type at path " + path);
        if (declaredType.isMapLikeType()) return NodeKind.OBJECT;
        if (declaredType.isCollectionLikeType() || declaredType.isArrayType()) return NodeKind.COLLECTION;

        var serializer = serializerFor(declaredType, path);
        if (serializer instanceof ContainerSerializer<?>) return NodeKind.COLLECTION;
        if (serializer instanceof BeanSerializerBase) return NodeKind.OBJECT;
        return NodeKind.SCALAR;
    }

    private BeanDescription introspect(JavaType type) {
        try {
            return objectMapper.getSerializationConfig().introspect(type);
        } catch (Exception e) {
            throw new ObjectGraphException("Failed to introspect type " + type, e);
        }
    }

    private com.fasterxml.jackson.databind.JsonSerializer<?> serializerFor(JavaType type, NodePath path) {
        try {
            return objectMapper.getSerializerProviderInstance().findValueSerializer(type);
        } catch (Exception e) {
            throw fail("Cannot resolve serializer for type " + type, path, type, e);
        }
    }

    private Object readPropertyValue(AnnotatedMember accessor, Object instance, String propertyName, NodePath path, JavaType javaType) {
        try {
            accessor.fixAccess(objectMapper.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            return accessor.getValue(instance);
        } catch (Exception e) {
            throw fail("Cannot read property '" + propertyName + "'", path, javaType, e);
        }
    }

    private Class<?> resolveJavaClass(JavaType declaredType, Object value) {
        if (declaredType != null && declaredType.getRawClass() != null) return declaredType.getRawClass();
        if (value != null) return value.getClass();
        throw new ObjectGraphException("Cannot determine javaType for node");
    }

    private JavaType resolveFallbackType(Object value) {
        return value == null ? null : objectMapper.constructType(value.getClass());
    }

    private ObjectGraphException fail(String message, NodePath path, JavaType javaType, Throwable cause) {
        StringBuilder builder = new StringBuilder(message);
        if (path != null) builder.append(" [path=").append(path).append("]");
        if (javaType != null) builder.append(" [javaType=").append(javaType).append("]");
        return cause == null ? new ObjectGraphException(builder.toString()) : new ObjectGraphException(builder.toString(), cause);
    }
}
