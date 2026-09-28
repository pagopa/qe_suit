package it.pagopa.infrastructure.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TestLogGrouper {

    private static final int TEST_CASE_OPEN_BRACKET_INDEX = 30;
    private static final int TEST_CASE_VALUE_INDEX = 31;
    private static final int TEST_CASE_WIDTH = 48;
    private static final int TEST_CASE_CLOSE_BRACKET_INDEX = 79;

    private static final String STARTUP_GROUP = "00-startup";

    private TestLogGrouper() {
    }

    public static Map<String, StringBuilder> group(List<String> lines) {
        Map<String, StringBuilder> groups = new LinkedHashMap<>();

        String currentTestCase = null;

        for (String line : lines) {
            String testCaseId = extractTestCaseId(line);

            if (testCaseId != null) {
                currentTestCase = testCaseId;
            }

            String key = currentTestCase != null
                    ? currentTestCase
                    : STARTUP_GROUP;

            groups.computeIfAbsent(key, ignored -> new StringBuilder())
                    .append(line)
                    .append(System.lineSeparator());
        }

        return groups;
    }

    public static String extractTestCaseId(String line) {
        if (line == null || line.length() <= TEST_CASE_CLOSE_BRACKET_INDEX) {
            return null;
        }

        if (line.charAt(TEST_CASE_OPEN_BRACKET_INDEX) != '[') {
            return null;
        }

        if (line.charAt(TEST_CASE_CLOSE_BRACKET_INDEX) != ']') {
            return null;
        }

        String value = line.substring(
                TEST_CASE_VALUE_INDEX,
                TEST_CASE_VALUE_INDEX + TEST_CASE_WIDTH
        ).stripTrailing();

        return value.isBlank() ? null : value;
    }

    public static int groupFile(Path source, Path target) throws IOException {
        List<String> lines = Files.readAllLines(source);

        Map<String, StringBuilder> groups = group(lines);

        StringBuilder output = new StringBuilder();

        for (Map.Entry<String, StringBuilder> entry : groups.entrySet()) {
            output.append(System.lineSeparator());
            output.append("========================================================================")
                    .append(System.lineSeparator());

            output.append("  TEST CASE: ")
                    .append(entry.getKey())
                    .append(System.lineSeparator());

            output.append("========================================================================")
                    .append(System.lineSeparator());

            output.append(entry.getValue());
        }

        Path parent = target.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(target, output.toString());

        return groups.size();
    }
}