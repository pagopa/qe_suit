package it.pagopa.interop.common.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@FunctionalInterface
interface NodeWalker {
    void visit(
            JsonNode jsonNode,
            Object javaValue,
            JavaType declaredType,
            NodePath path,
            List<Node> nodes,
            NodePath parentPath,
            QueryStep stepFromParent
    );
}
