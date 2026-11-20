package it.pagopa.interop.common.infrastructure.copy;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ExpectedErrorMessages {

    private static final String RESOURCE = "error-messages/expected-error-messages.json";
    private final List<Message> messages;
    private final List<SharedText> sharedTexts;
    private final Map<String, Map<String, String>> copies = new HashMap<>();

    public record Message(String id, String process, String error, String bffEndpoint, Map<String, String> copy) {
        public Message {
            copy = Map.copyOf(copy);
        }
    }

    public record SharedText(String id, Map<String, String> copy) {
        public SharedText {
            copy = Map.copyOf(copy);
        }
    }

    private ExpectedErrorMessages(JsonNode root) {
        fields(root, Set.of("schemaVersion", "messages", "sharedTexts"), "catalog");
        JsonNode version = root.get("schemaVersion");
        if (!version.isIntegralNumber() || !version.canConvertToInt() || version.intValue() != 1) {
            throw invalid("schemaVersion must be 1");
        }
        List<Message> parsedMessages = new ArrayList<>();
        Set<List<String>> mappings = new HashSet<>();
        for (JsonNode node : array(root, "messages")) {
            fields(node, Set.of("id", "process", "error", "bffEndpoint", "copy"), "message");
            Message message = new Message(
                    text(node, "id"), text(node, "process"), text(node, "error"),
                    text(node, "bffEndpoint"), copy(node));
            if (!mappings.add(List.of(message.process(), message.error(), message.bffEndpoint()))) {
                throw invalid("Duplicate mapping for " + message.id());
            }
            register(message.id(), message.copy());
            parsedMessages.add(message);
        }
        List<SharedText> parsedSharedTexts = new ArrayList<>();
        for (JsonNode node : array(root, "sharedTexts")) {
            fields(node, Set.of("id", "copy"), "shared text");
            SharedText shared = new SharedText(text(node, "id"), copy(node));
            register(shared.id(), shared.copy());
            parsedSharedTexts.add(shared);
        }
        messages = List.copyOf(parsedMessages);
        sharedTexts = List.copyOf(parsedSharedTexts);
    }

    public static ExpectedErrorMessages load() {
        try (InputStream input = ExpectedErrorMessages.class.getClassLoader().getResourceAsStream(RESOURCE)) {
            if (input == null) {
                throw invalid("Missing classpath resource " + RESOURCE);
            }
            return read(input);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot close copy catalog " + RESOURCE, e);
        }
    }

    public static ExpectedErrorMessages read(InputStream input) {
        if (input == null) {
            throw invalid("Catalog input is required");
        }
        ObjectMapper mapper = new ObjectMapper()
                .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        try {
            return new ExpectedErrorMessages(mapper.readTree(input));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read copy catalog", e);
        }
    }

    public List<Message> messages() {
        return messages;
    }

    public List<SharedText> sharedTexts() {
        return sharedTexts;
    }

    public String text(String id, String language) {
        if (!Set.of("it", "en").contains(language == null ? "" : language)) {
            throw invalid("Unsupported copy language: " + language);
        }
        Map<String, String> translations = copies.get(id);
        if (translations == null) {
            throw invalid("Unknown copy ID: " + id);
        }
        String result = translations.get(language);
        if (result == null) {
            throw invalid("Missing " + language + " translation for " + id);
        }
        return result;
    }

    public String render(String id, String language, Map<String, String> values) {
        return CopyTemplateResolver.render(text(id, language), values);
    }

    private void register(String id, Map<String, String> copy) {
        if (!id.matches("[A-Z][A-Z0-9_]*")) {
            throw invalid("Invalid copy ID: " + id);
        }
        if (copies.putIfAbsent(id, copy) != null) {
            throw invalid("Duplicate copy ID: " + id);
        }
    }

    private static Map<String, String> copy(JsonNode node) {
        JsonNode copy = node.get("copy");
        if (copy == null || !copy.isObject() || !copy.has("it")) {
            throw invalid("copy must be an object containing it");
        }
        Map<String, String> result = new HashMap<>();
        copy.fieldNames().forEachRemaining(language -> {
            if (!Set.of("it", "en").contains(language)) {
                throw invalid("Unsupported copy language: " + language);
            }
            String value = text(copy, language);
            CopyTemplateResolver.placeholders(value);
            result.put(language, value);
        });
        return Map.copyOf(result);
    }

    private static JsonNode array(JsonNode node, String name) {
        JsonNode array = node.get(name);
        if (!array.isArray()) {
            throw invalid(name + " must be an array");
        }
        return array;
    }

    private static String text(JsonNode node, String name) {
        JsonNode value = node.get(name);
        if (value == null || !value.isTextual() || value.textValue().isBlank()) {
            throw invalid(name + " must be a nonblank string");
        }
        String result = value.textValue();
        if (!Set.of("it", "en").contains(name) && !result.equals(result.strip())) {
            throw invalid(name + " must not have surrounding whitespace");
        }
        return result;
    }

    private static void fields(JsonNode node, Set<String> expected, String context) {
        if (node == null || !node.isObject()) {
            throw invalid(context + " must be an object");
        }
        Set<String> actual = new HashSet<>();
        node.fieldNames().forEachRemaining(actual::add);
        if (!actual.equals(expected)) {
            throw invalid(context + " must contain exactly " + expected);
        }
    }

    private static IllegalArgumentException invalid(String message) {
        return new IllegalArgumentException(message);
    }
}
