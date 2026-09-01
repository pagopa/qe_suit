package it.pagopa.infrastructure.objectgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class NodePath {

    private static final NodePath ROOT = new NodePath("");

    private final String pointer;

    private NodePath(String pointer) {
        if (pointer == null) {
            throw new IllegalArgumentException("pointer must not be null");
        }
        if (!pointer.isEmpty() && !pointer.startsWith("/")) {
            throw new IllegalArgumentException("Invalid JSON Pointer: " + pointer);
        }
        this.pointer = pointer;
    }

    public static NodePath root() {
        return ROOT;
    }

    public boolean isRoot() {
        return pointer.isEmpty();
    }

    public Optional<NodePath> parent() {
        if (isRoot()) {
            return Optional.empty();
        }

        int lastSlash = pointer.lastIndexOf('/');
        if (lastSlash == 0) {
            return Optional.of(root());
        }
        return Optional.of(new NodePath(pointer.substring(0, lastSlash)));
    }

    public boolean isDirectChildOf(NodePath other) {
        Objects.requireNonNull(other, "other must not be null");
        return parent().map(other::equals).orElse(false);
    }

    public String printable() {
        if (isRoot()) {
            return "<root>";
        }

        StringJoiner joiner = new StringJoiner("");
        for (String token : tokens()) {
            if (isIndexToken(token)) {
                joiner.add("[" + token + "]");
            } else if (joiner.length() == 0) {
                joiner.add(token);
            } else {
                joiner.add("." + token);
            }
        }
        return joiner.toString();
    }

    @Override
    public String toString() {
        return pointer;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof NodePath nodePath)) return false;
        return pointer.equals(nodePath.pointer);
    }

    @Override
    public int hashCode() {
        return pointer.hashCode();
    }

    NodePath property(String propertyName) {
        Objects.requireNonNull(propertyName, "propertyName must not be null");
        String escaped = escape(propertyName);
        return new NodePath(pointer + "/" + escaped);
    }

    NodePath index(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be >= 0");
        }
        return new NodePath(pointer + "/" + index);
    }

    private List<String> tokens() {
        String[] rawTokens = pointer.substring(1).split("/", -1);
        List<String> tokens = new ArrayList<>(rawTokens.length);
        for (String rawToken : rawTokens) {
            tokens.add(unescape(rawToken));
        }
        return tokens;
    }

    private boolean isIndexToken(String token) {
        if (token.isEmpty()) {
            return false;
        }
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private String escape(String token) {
        return token.replace("~", "~0").replace("/", "~1");
    }

    private String unescape(String token) {
        return token.replace("~1", "/").replace("~0", "~");
    }
}
