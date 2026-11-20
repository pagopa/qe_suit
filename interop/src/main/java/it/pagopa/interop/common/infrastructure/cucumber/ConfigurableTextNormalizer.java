package it.pagopa.interop.common.infrastructure.cucumber;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import it.pagopa.interop.common.kernel.dto.ConfigurableText;
import it.pagopa.interop.common.kernel.dto.TextGenerationMode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Translates opt-in textual column modifiers to Jackson-bindable value objects. */
public final class ConfigurableTextNormalizer {
    private final ConfigurableTextHeaderSyntax syntax = new ConfigurableTextHeaderSyntax();

    public Map<String, Object> normalize(Map<String, String> input, Class<?> dtoType,
                                         DeserializationConfig config) {
        JavaType javaType = config.constructType(dtoType);
        BeanDescription bean = config.introspect(javaType);
        Map<String, String> configurableNames = new HashMap<>();
        for (BeanPropertyDefinition property : bean.findProperties()) {
            if (property.getPrimaryType().getRawClass() != ConfigurableText.class) {
                continue;
            }
            configurableNames.put(property.getName(), property.getName());
            property.findAliases().forEach(alias -> configurableNames.put(alias.getSimpleName(), property.getName()));
        }

        Map<String, Object> normalized = new HashMap<>();
        Set<String> seen = new HashSet<>();
        for (var entry : input.entrySet()) {
            String header = entry.getKey();
            var parsed = syntax.parse(header);
            String canonical = configurableNames.get(parsed.label());
            if (canonical == null) {
                // Unrecognized headers remain subject to Jackson's usual unknown-field checks.
                normalized.put(header, entry.getValue());
                continue;
            }
            if (parsed.modified() && parsed.mode() == null) {
                throw new IllegalArgumentException("Unsupported text modifier in column: " + header);
            }
            if (!seen.add(canonical)) {
                throw new IllegalArgumentException("Duplicate configuration for field: " + canonical);
            }
            Map<String, Object> text = new HashMap<>();
            text.put("value", entry.getValue());
            text.put("mode", parsed.modified() ? parsed.mode() : TextGenerationMode.RANDOM);
            normalized.put(canonical, text);
        }
        return normalized;
    }
}
