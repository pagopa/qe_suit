package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.pagopa.infrastructure.objectgraph.NodePath;

import java.util.Objects;

public final class JacksonFuzzMutationApplier implements FuzzMutationApplier {
    private final ObjectMapper objectMapper;

    public JacksonFuzzMutationApplier(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    @Override
    public JsonNode apply(JsonNode target, NodePath path, FuzzMutation mutation) {
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(mutation, "mutation must not be null");

        try {
            if (path.isRoot()) return applyRoot(mutation);
            return applyNested(target, path, mutation);
        } catch (FuzzingException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new FuzzingException("Failed to apply mutation at path: " + path, exception);
        }
    }

    private JsonNode applyRoot(FuzzMutation mutation) {
        if (mutation.kind() == FuzzMutationKind.REMOVE) return null;
        if (mutation.value() == null) return NullNode.getInstance();
        return objectMapper.valueToTree(mutation.value());
    }

    private JsonNode applyNested(JsonNode target, NodePath path, FuzzMutation mutation) {
        NodePath parentPath = path.parent()
                .orElseThrow(() -> new FuzzingException("Missing parent for path: " + path));

        JsonNode parentNode = target.at(parentPath.toString());
        if (parentNode.isMissingNode()) {
            throw new FuzzingException("Parent path not found: " + parentPath);
        }

        String token = lastToken(path.toString());

        if (parentNode instanceof ObjectNode objectNode) {
            mutateObjectChild(objectNode, token, mutation);
            return target;
        }
        if (parentNode instanceof ArrayNode arrayNode) {
            mutateArrayChild(arrayNode, token, mutation, path);
            return target;
        }

        throw new FuzzingException(
                "Unsupported parent node type at path " + parentPath + ": " + parentNode.getNodeType()
        );
    }

    private void mutateObjectChild(ObjectNode node, String token, FuzzMutation mutation) {
        String field = unescape(token);
        if (mutation.kind() == FuzzMutationKind.REMOVE) {
            node.remove(field);
        } else {
            node.set(field, mutation.value() == null
                    ? NullNode.getInstance()
                    : objectMapper.valueToTree(mutation.value()));
        }
    }

    private void mutateArrayChild(ArrayNode node, String token, FuzzMutation mutation, NodePath path) {
        int index = parseArrayIndex(token, path);
        if (index < 0 || index >= node.size()) {
            throw new FuzzingException("Array index out of bounds for path: " + path);
        }

        if (mutation.kind() == FuzzMutationKind.REMOVE) {
            node.remove(index);
        } else {
            node.set(index, mutation.value() == null
                    ? NullNode.getInstance()
                    : objectMapper.valueToTree(mutation.value()));
        }
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