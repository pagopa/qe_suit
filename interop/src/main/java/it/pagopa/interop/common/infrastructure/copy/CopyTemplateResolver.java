package it.pagopa.interop.common.infrastructure.copy;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CopyTemplateResolver {

    private static final Pattern TOKEN = Pattern.compile("\\{([^{}]*)}");
    private static final Pattern NAME = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");

    private CopyTemplateResolver() {
    }

    public static Set<String> placeholders(String template) {
        if (template == null) {
            throw new IllegalArgumentException("Copy template is required");
        }
        String remainder = TOKEN.matcher(template).replaceAll("");
        if (remainder.contains("{") || remainder.contains("}")) {
            throw new IllegalArgumentException("Unbalanced copy placeholder in: " + template);
        }
        Set<String> names = new LinkedHashSet<>();
        Matcher matcher = TOKEN.matcher(template);
        while (matcher.find()) {
            String name = matcher.group(1);
            if (!NAME.matcher(name).matches()) {
                throw new IllegalArgumentException("Invalid copy placeholder: {" + name + "}");
            }
            names.add(name);
        }
        return Set.copyOf(names);
    }

    public static String render(String template, Map<String, String> values) {
        Set<String> names = placeholders(template);
        if (values == null) {
            throw new IllegalArgumentException("Copy placeholder values are required");
        }
        for (String name : names) {
            if (values.get(name) == null) {
                throw new IllegalArgumentException("Missing copy placeholder value: " + name);
            }
        }
        for (String name : values.keySet()) {
            if (!names.contains(name)) {
                throw new IllegalArgumentException("Unexpected copy placeholder value: " + name);
            }
        }
        Matcher matcher = TOKEN.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(values.get(matcher.group(1))));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
