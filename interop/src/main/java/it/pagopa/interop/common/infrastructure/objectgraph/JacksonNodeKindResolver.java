package it.pagopa.interop.common.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

import java.util.Objects;

final class JacksonNodeKindResolver {

    private final ObjectMapper objectMapper;

    JacksonNodeKindResolver(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    NodeKind resolveNodeKind(JsonNode jsonNode, JavaType declaredType, NodePath path) {
        if (jsonNode.isObject()) return NodeKind.OBJECT;
        if (jsonNode.isArray()) return NodeKind.COLLECTION;
        if (!jsonNode.isNull()) return NodeKind.SCALAR;
        return inferNullKind(declaredType, path);
    }

    private NodeKind inferNullKind(JavaType declaredType, NodePath path) {
        if (declaredType == null) {
            throw new ObjectGraphException("Cannot infer null node kind without declared type at path " + path);
        }
        if (declaredType.isMapLikeType()) return NodeKind.OBJECT;
        if (declaredType.isCollectionLikeType() || declaredType.isArrayType()) return NodeKind.COLLECTION;

        var serializer = serializerFor(declaredType, path);
        if (serializer instanceof ContainerSerializer<?>) return NodeKind.COLLECTION;
        if (serializer instanceof BeanSerializerBase) return NodeKind.OBJECT;
        return NodeKind.SCALAR;
    }

    private com.fasterxml.jackson.databind.JsonSerializer<?> serializerFor(JavaType type, NodePath path) {
        try {
            return objectMapper.getSerializerProviderInstance().findValueSerializer(type);
        } catch (Exception e) {
            throw ObjectGraphErrors.fail("Cannot resolve serializer for type " + type, path, type, e);
        }
    }
}
