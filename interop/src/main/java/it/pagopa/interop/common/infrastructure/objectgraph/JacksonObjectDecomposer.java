package it.pagopa.interop.common.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

final class JacksonObjectDecomposer implements ObjectDecomposer {

    private final ObjectMapper objectMapper;
    private final JacksonGraphWalker graphWalker;

    JacksonObjectDecomposer(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.graphWalker = new JacksonGraphWalker(objectMapper);
    }

    @Override
    public ObjectGraph decompose(Object source) {
        if (source == null) {
            throw new ObjectGraphException("source must not be null");
        }

        JavaType rootType = objectMapper.constructType(source.getClass());
        JsonNode rootJson = toJsonTree(source, rootType);
        if (rootJson.isNull()) {
            throw new ObjectGraphException("Root null JSON is not supported for type: " + rootType);
        }

        List<Node> nodes = graphWalker.decompose(rootJson, source, rootType);
        return new ObjectGraph(nodes);
    }

    private JsonNode toJsonTree(Object source, JavaType rootType) {
        try {
            return objectMapper.valueToTree(source);
        } catch (Exception e) {
            throw new ObjectGraphException("Failed to serialize source type " + rootType, e);
        }
    }
}
