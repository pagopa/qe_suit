package it.pagopa.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class JacksonGraphWalker {

    private final JacksonNodeKindResolver nodeKindResolver;
    private final JacksonObjectCorrelation objectCorrelation;
    private final JacksonCollectionCorrelation collectionCorrelation;

    JacksonGraphWalker(ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.nodeKindResolver = new JacksonNodeKindResolver(objectMapper);
        this.objectCorrelation = new JacksonObjectCorrelation(objectMapper);
        this.collectionCorrelation = new JacksonCollectionCorrelation(objectMapper);
    }

    GraphDecomposition decompose(JsonNode rootJson, Object source, JavaType rootType) {
        List<Node> nodes = new ArrayList<>();
        Map<QueryEdge, NodePath> queryEdges = new HashMap<>();
        visit(rootJson, source, rootType, NodePath.root(), nodes, null, null, queryEdges);
        return new GraphDecomposition(nodes, queryEdges);
    }

    void visit(
            JsonNode jsonNode,
            Object javaValue,
            JavaType declaredType,
            NodePath path,
            List<Node> nodes,
            NodePath parentPath,
            QueryStep stepFromParent,
            Map<QueryEdge, NodePath> queryEdges
    ) {
        NodeKind kind = nodeKindResolver.resolveNodeKind(jsonNode, declaredType, path);
        nodes.add(new Node(kind, path, javaValue, resolveJavaClass(declaredType, javaValue)));
        if (parentPath != null && stepFromParent != null) {
            queryEdges.put(new QueryEdge(parentPath, stepFromParent), path);
        }

        if (jsonNode.isNull() || kind == NodeKind.SCALAR) {
            return;
        }

        if (kind == NodeKind.OBJECT) {
            if (!(jsonNode instanceof ObjectNode objectNode)) {
                throw ObjectGraphErrors.fail("Expected JSON object", path, declaredType, null);
            }
            objectCorrelation.correlate(
                    objectNode,
                    javaValue,
                    declaredType,
                    path,
                    nodes,
                    (childJson, childValue, childType, childPath, allNodes, edgeParent, edgeStep) ->
                            visit(childJson, childValue, childType, childPath, allNodes, edgeParent, edgeStep, queryEdges)
            );
            return;
        }

        if (!(jsonNode instanceof ArrayNode arrayNode)) {
            throw ObjectGraphErrors.fail("Expected JSON array", path, declaredType, null);
        }
        collectionCorrelation.correlate(
                arrayNode,
                javaValue,
                declaredType,
                path,
                nodes,
                (childJson, childValue, childType, childPath, allNodes, edgeParent, edgeStep) ->
                        visit(childJson, childValue, childType, childPath, allNodes, edgeParent, edgeStep, queryEdges)
        );
    }

    private Class<?> resolveJavaClass(JavaType declaredType, Object value) {
        if (declaredType != null && declaredType.getRawClass() != null) return declaredType.getRawClass();
        if (value != null) return value.getClass();
        throw new ObjectGraphException("Cannot determine javaType for node");
    }
}
