package it.pagopa.reporting.writer.html;

import it.pagopa.reporting.dto.annotation.ReportField;
import it.pagopa.reporting.dto.annotation.ReportFieldFormat;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

final class RecursiveDtoHtmlRenderer {

    private RecursiveDtoHtmlRenderer() {
    }

    static String render(Object dtoRoot) {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"dto-dump\">");
        renderValue(dtoRoot, html, new IdentityHashMap<>(), 0);
        html.append("</div>");
        return html.toString();
    }

    private static void renderValue(Object value, StringBuilder html, IdentityHashMap<Object, Boolean> visited, int depth) {
        if (value == null) {
            html.append("<span class=\"null\">null</span>");
            return;
        }

        if (isScalar(value)) {
            html.append(formatScalar(value, ReportFieldFormat.AUTO));
            return;
        }

        if (depth > 12) {
            html.append("<span class=\"truncated\">&lt;max-depth&gt;</span>");
            return;
        }

        if (visited.put(value, Boolean.TRUE) != null) {
            html.append("<span class=\"circular\">&lt;circular-reference&gt;</span>");
            return;
        }

        if (value instanceof Map<?, ?> map) {
            html.append("<ul class=\"dto-list\">");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                html.append("<li>");
                html.append("<span class=\"dto-key\">").append(escapeHtml(String.valueOf(entry.getKey()))).append("</span>");
                html.append(": ");
                renderValue(entry.getValue(), html, visited, depth + 1);
                html.append("</li>");
            }
            html.append("</ul>");
            visited.remove(value);
            return;
        }

        if (value instanceof Collection<?> collection) {
            html.append("<ol class=\"dto-list\">");
            for (Object item : collection) {
                html.append("<li>");
                renderValue(item, html, visited, depth + 1);
                html.append("</li>");
            }
            html.append("</ol>");
            visited.remove(value);
            return;
        }

        if (value.getClass().isArray()) {
            html.append("<ol class=\"dto-list\">");
            int size = Array.getLength(value);
            for (int i = 0; i < size; i++) {
                html.append("<li>");
                renderValue(Array.get(value, i), html, visited, depth + 1);
                html.append("</li>");
            }
            html.append("</ol>");
            visited.remove(value);
            return;
        }

        if (value.getClass().isRecord()) {
            html.append("<ul class=\"dto-list\">");
            for (RecordField field : orderedRecordFields(value.getClass())) {
                html.append("<li>");
                html.append("<span class=\"dto-key\">")
                        .append(escapeHtml(field.label()))
                        .append("</span>")
                        .append(": ");
                try {
                    Object childValue = field.accessor().invoke(value);
                    if (field.format() == ReportFieldFormat.CODE_BLOCK || field.format() == ReportFieldFormat.MULTILINE) {
                        if (childValue == null) {
                            html.append("<span class=\"null\">null</span>");
                        } else {
                            html.append("<pre>").append(escapeHtml(String.valueOf(childValue))).append("</pre>");
                        }
                    } else {
                        renderValue(childValue, html, visited, depth + 1);
                    }
                } catch (ReflectiveOperationException e) {
                    html.append("<span class=\"error\">&lt;reflection-error&gt;</span>");
                }
                html.append("</li>");
            }
            html.append("</ul>");
            visited.remove(value);
            return;
        }

        html.append("<span>").append(escapeHtml(String.valueOf(value))).append("</span>");
        visited.remove(value);
    }

    private static List<RecordField> orderedRecordFields(Class<?> recordClass) {
        List<RecordField> fields = new ArrayList<>();
        for (RecordComponent component : recordClass.getRecordComponents()) {
            ReportField annotation = component.getAnnotation(ReportField.class);
            String label = annotation != null && !annotation.label().isBlank() ? annotation.label() : component.getName();
            int order = annotation != null ? annotation.order() : 100;
            ReportFieldFormat format = annotation != null ? annotation.format() : ReportFieldFormat.AUTO;
            fields.add(new RecordField(component.getAccessor(), label, order, format));
        }
        fields.sort(Comparator.comparingInt(RecordField::order).thenComparing(RecordField::label));
        return fields;
    }

    private static boolean isScalar(Object value) {
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Enum<?>
                || value instanceof Character
                || value instanceof Temporal;
    }

    private static String formatScalar(Object value, ReportFieldFormat format) {
        if (value == null) {
            return "<span class=\"null\">null</span>";
        }

        String text = switch (format) {
            case DURATION_SECONDS -> String.format("%.3f", value);
            default -> String.valueOf(value);
        };
        return "<span>" + escapeHtml(text) + "</span>";
    }

    static String escapeHtml(String raw) {
        if (raw == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            switch (c) {
                case '&' -> escaped.append("&amp;");
                case '<' -> escaped.append("&lt;");
                case '>' -> escaped.append("&gt;");
                case '"' -> escaped.append("&quot;");
                case '\'' -> escaped.append("&#39;");
                default -> escaped.append(c);
            }
        }
        return escaped.toString();
    }

    private record RecordField(Method accessor, String label, int order, ReportFieldFormat format) {
    }
}

