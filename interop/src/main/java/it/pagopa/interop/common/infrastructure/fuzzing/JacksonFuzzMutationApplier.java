package it.pagopa.interop.common.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class JacksonFuzzMutationApplier implements FuzzMutationApplier {

    private final ObjectMapper objectMapper;

    JacksonFuzzMutationApplier(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    @Override
    public JsonNode apply(JsonNode target, NodePath path, FuzzMutation mutation) {
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(mutation, "mutation must not be null");

        try {
            if (path.isRoot()) {
                return applyRoot(mutation);
            }
            return applyNested(target, path, mutation);
        } catch (FuzzingException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new FuzzingException("Failed to apply mutation at path: " + path, exception);
        }
    }

    private JsonNode applyRoot(FuzzMutation mutation) {
        if (mutation.kind() == FuzzMutationKind.REMOVE) {
            return null;
        }
        if (mutation.value() == null) {
            return NullNode.getInstance();
        }
        return objectMapper.valueToTree(mutation.value());
    }

    private JsonNode applyNested(JsonNode target, NodePath path, FuzzMutation mutation) {
        NodePath parentPath = path.parent().orElseThrow(() -> new FuzzingException("Missing parent for path: " + path));
        JsonNode parentNode = target.at(parentPath.toString());
        if (parentNode.isMissingNode()) {
            throw new FuzzingException("Parent path not found: " + parentPath);
        }

        String childToken = lastToken(path.toString());
        if (parentNode instanceof ObjectNode objectNode) {
            mutateObjectChild(objectNode, childToken, mutation, path);
            return target;
        }
        if (parentNode instanceof ArrayNode arrayNode) {
            mutateArrayChild(arrayNode, childToken, mutation, path);
            return target;
        }
        throw new FuzzingException("Unsupported parent node type at path " + parentPath + ": " + parentNode.getNodeType());
    }

    private void mutateObjectChild(ObjectNode objectNode, String escapedToken, FuzzMutation mutation, NodePath path) {
        String field = unescape(escapedToken);
        if (mutation.kind() == FuzzMutationKind.REMOVE) {
            objectNode.remove(field);
            return;
        }
        objectNode.set(field, mutation.value() == null ? NullNode.getInstance() : objectMapper.valueToTree(mutation.value()));
    }

    private void mutateArrayChild(ArrayNode arrayNode, String token, FuzzMutation mutation, NodePath path) {
        int index = parseArrayIndex(token, path);
        if (index < 0 || index >= arrayNode.size()) {
            throw new FuzzingException("Array index out of bounds for path: " + path);
        }
        if (mutation.kind() == FuzzMutationKind.REMOVE) {
            arrayNode.remove(index);
            return;
        }
        arrayNode.set(index, mutation.value() == null ? NullNode.getInstance() : objectMapper.valueToTree(mutation.value()));
    }

    private String lastToken(String pointer) {
        int slash = pointer.lastIndexOf('/');
        if (slash < 0 || slash == pointer.length() - 1) {
            throw new FuzzingException("Invalid path pointer: " + pointer);
        }
        return pointer.substring(slash + 1);
    }

    private int parseArrayIndex(String token, NodePath path) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException exception) {
            throw new FuzzingException("Invalid array index for path: " + path, exception);
        }
    }

    private String unescape(String token) {
        return token.replace("~1", "/").replace("~0", "~");
    }
}
