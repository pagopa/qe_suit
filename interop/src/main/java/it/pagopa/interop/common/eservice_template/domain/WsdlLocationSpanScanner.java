package it.pagopa.interop.common.eservice_template.domain;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class WsdlLocationSpanScanner {
    private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final String SOAP12_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/";

    List<Span> scan(byte[] document) {
        List<Span> spans = new ArrayList<>();
        Deque<Map<String, String>> namespaceScopes = new ArrayDeque<>();
        int index = 0;
        while (index < document.length) {
            if (document[index] != '<') {
                index++;
                continue;
            }
            if (startsWith(document, index, "<!--")) {
                index = after(document, index + 4, "-->");
                continue;
            }
            if (startsWith(document, index, "<![CDATA[")) {
                index = after(document, index + 9, "]]>");
                continue;
            }
            if (startsWith(document, index, "<?")) {
                index = after(document, index + 2, "?>");
                continue;
            }
            if (startsWith(document, index, "<!")) {
                index = afterDeclaration(document, index + 2);
                continue;
            }
            if (startsWith(document, index, "</")) {
                index = after(document, index + 2, ">");
                if (!namespaceScopes.isEmpty()) namespaceScopes.pop();
                continue;
            }

            Tag tag = readTag(document, index, namespaceScopes);
            if ("address".equals(tag.localName())
                    && (SOAP11_NAMESPACE.equals(tag.namespace()) || SOAP12_NAMESPACE.equals(tag.namespace()))) {
                tag.attributes().stream()
                        .filter(attribute -> "location".equals(attribute.name()))
                        .map(Attribute::span)
                        .forEach(spans::add);
            }
            if (!tag.selfClosing()) namespaceScopes.push(tag.namespaces());
            index = tag.end();
        }
        return spans;
    }

    private static Tag readTag(byte[] document, int index, Deque<Map<String, String>> scopes) {
        int cursor = index + 1;
        int nameStart = cursor;
        while (cursor < document.length && !isDelimiter(document[cursor])) cursor++;
        String qualifiedName = utf8(document, nameStart, cursor);
        int separator = qualifiedName.indexOf(':');
        String prefix = separator < 0 ? "" : qualifiedName.substring(0, separator);
        String localName = separator < 0 ? qualifiedName : qualifiedName.substring(separator + 1);
        Map<String, String> namespaces = scopes.isEmpty() ? new HashMap<>() : new HashMap<>(scopes.peek());
        List<Attribute> attributes = new ArrayList<>();
        boolean selfClosing = false;
        while (cursor < document.length) {
            cursor = skipWhitespace(document, cursor);
            if (cursor >= document.length || document[cursor] == '>') {
                cursor++;
                break;
            }
            if (document[cursor] == '/') {
                selfClosing = true;
                cursor = after(document, cursor + 1, ">");
                break;
            }
            Attribute attribute = readAttribute(document, cursor);
            attributes.add(attribute);
            cursor = attribute.end();
        }
        for (Attribute attribute : attributes) {
            if ("xmlns".equals(attribute.name())) namespaces.put("", attribute.value());
            else if (attribute.name().startsWith("xmlns:")) {
                namespaces.put(attribute.name().substring("xmlns:".length()), attribute.value());
            }
        }
        return new Tag(
                namespaces.get(prefix), localName, namespaces, attributes, selfClosing, cursor
        );
    }

    private static Attribute readAttribute(byte[] bytes, int start) {
        int cursor = start;
        while (cursor < bytes.length && !isAttributeDelimiter(bytes[cursor])) cursor++;
        String name = utf8(bytes, start, cursor);
        cursor = skipWhitespace(bytes, cursor);
        if (cursor >= bytes.length || bytes[cursor] != '=') {
            throw new AssertionError("Attributo XML non valido nel WSDL");
        }
        cursor = skipWhitespace(bytes, cursor + 1);
        if (cursor >= bytes.length || (bytes[cursor] != '\'' && bytes[cursor] != '"')) {
            throw new AssertionError("Valore attributo XML non quotato nel WSDL");
        }
        byte quote = bytes[cursor++];
        int valueStart = cursor;
        while (cursor < bytes.length && bytes[cursor] != quote) cursor++;
        if (cursor >= bytes.length) throw new AssertionError("Valore attributo XML non terminato nel WSDL");
        String value = utf8(bytes, valueStart, cursor);
        Span span = new Span(valueStart, cursor);
        return new Attribute(name, value, span, cursor + 1);
    }

    private static boolean startsWith(byte[] value, int offset, String token) {
        byte[] expected = token.getBytes(StandardCharsets.US_ASCII);
        if (offset + expected.length > value.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (value[offset + i] != expected[i]) return false;
        }
        return true;
    }

    private static int after(byte[] value, int offset, String token) {
        while (offset < value.length) {
            if (startsWith(value, offset, token)) return offset + token.length();
            offset++;
        }
        throw new AssertionError("Token XML non terminato: " + token);
    }

    private static int afterDeclaration(byte[] value, int offset) {
        byte quote = 0;
        int subsetDepth = 0;
        for (int i = offset; i < value.length; i++) {
            byte current = value[i];
            if (quote != 0) {
                if (current == quote) quote = 0;
            } else if (current == '\'' || current == '"') {
                quote = current;
            } else if (current == '[') {
                subsetDepth++;
            } else if (current == ']') {
                subsetDepth--;
            } else if (current == '>' && subsetDepth == 0) {
                return i + 1;
            }
        }
        throw new AssertionError("Dichiarazione XML non terminata");
    }

    private static int skipWhitespace(byte[] value, int offset) {
        while (offset < value.length && Character.isWhitespace((char) value[offset])) offset++;
        return offset;
    }

    private static boolean isDelimiter(byte value) {
        return Character.isWhitespace((char) value) || value == '/' || value == '>';
    }

    private static boolean isAttributeDelimiter(byte value) {
        return Character.isWhitespace((char) value) || value == '=' || value == '/' || value == '>';
    }

    private static String utf8(byte[] value, int start, int end) {
        return new String(value, start, end - start, StandardCharsets.UTF_8);
    }

    record Span(int start, int end) {}

    private record Attribute(String name, String value, Span span, int end) {}

    private record Tag(
            String namespace,
            String localName,
            Map<String, String> namespaces,
            List<Attribute> attributes,
            boolean selfClosing,
            int end
    ) {}
}
