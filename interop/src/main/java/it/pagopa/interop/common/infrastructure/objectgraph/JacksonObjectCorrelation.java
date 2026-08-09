package it.pagopa.interop.common.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class JacksonObjectCorrelation {

    private final ObjectMapper objectMapper;

    JacksonObjectCorrelation(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    void correlate(ObjectNode objectNode, Object javaValue, JavaType declaredType, NodePath path, List<Node> nodes, NodeWalker walker) {
        if (javaValue == null) return;
        if (javaValue instanceof Map<?, ?> map) {
            correlateMap(objectNode, map, declaredType, path, nodes, walker);
            return;
        }
        correlatePojo(objectNode, javaValue, declaredType, path, nodes, walker);
    }

    private void correlatePojo(ObjectNode objectNode, Object javaValue, JavaType declaredType, NodePath path, List<Node> nodes, NodeWalker walker) {
        BeanDescription description = introspect(declaredType);
        List<BeanPropertyDefinition> properties = description.findProperties();
        int visitedChildren = 0;

        for (BeanPropertyDefinition property : properties) {
            AnnotatedMember accessor = property.getAccessor();
            if (accessor == null) {
                throw ObjectGraphErrors.fail("Property accessor not available for '" + property.getName() + "'", path, declaredType, null);
            }

            String jsonName = property.getName();
            JsonNode childJson = objectNode.get(jsonName);
            if (childJson == null) {
                throw ObjectGraphErrors.fail("Cannot correlate property '" + jsonName + "' with JSON object field", path, declaredType, null);
            }

            Object childValue = readPropertyValue(accessor, javaValue, jsonName, path, declaredType);
            walker.visit(childJson, childValue, accessor.getType(), path.property(jsonName), nodes);
            visitedChildren++;
        }

        if (visitedChildren != objectNode.size()) {
            throw ObjectGraphErrors.fail("JSON object contains fields that cannot be correlated", path, declaredType, null);
        }
    }

    private void correlateMap(ObjectNode objectNode, Map<?, ?> map, JavaType declaredType, NodePath path, List<Node> nodes, NodeWalker walker) {
        if (map.size() != objectNode.size()) {
            throw ObjectGraphErrors.fail("Map and JSON object cardinality mismatch", path, declaredType, null);
        }

        Iterator<? extends Map.Entry<?, ?>> mapIterator = map.entrySet().iterator();
        Iterator<Map.Entry<String, JsonNode>> jsonIterator = objectNode.fields();
        JavaType valueType = declaredType.getContentType();

        while (mapIterator.hasNext() && jsonIterator.hasNext()) {
            Object childValue = mapIterator.next().getValue();
            Map.Entry<String, JsonNode> jsonEntry = jsonIterator.next();
            JavaType childType = valueType != null ? valueType : resolveFallbackType(childValue);
            walker.visit(jsonEntry.getValue(), childValue, childType, path.property(jsonEntry.getKey()), nodes);
        }

        if (mapIterator.hasNext() || jsonIterator.hasNext()) {
            throw ObjectGraphErrors.fail("Map and JSON object cannot be correlated", path, declaredType, null);
        }
    }

    private BeanDescription introspect(JavaType type) {
        try {
            return objectMapper.getSerializationConfig().introspect(type);
        } catch (Exception e) {
            throw new ObjectGraphException("Failed to introspect type " + type, e);
        }
    }

    private Object readPropertyValue(AnnotatedMember accessor, Object instance, String propertyName, NodePath path, JavaType javaType) {
        try {
            accessor.fixAccess(objectMapper.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            return accessor.getValue(instance);
        } catch (Exception e) {
            throw ObjectGraphErrors.fail("Cannot read property '" + propertyName + "'", path, javaType, e);
        }
    }

    private JavaType resolveFallbackType(Object value) {
        return value == null ? null : objectMapper.constructType(value.getClass());
    }
}
