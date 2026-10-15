package it.pagopa.infrastructure.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TestLogGrouper {

    private static final int TEST_CASE_OPEN_BRACKET_INDEX = 30;
    private static final int TEST_CASE_VALUE_INDEX = 31;
    private static final int TEST_CASE_WIDTH = 48;
    private static final int TEST_CASE_CLOSE_BRACKET_INDEX = 79;

    private static final int TEST_EXECUTION_OPEN_BRACKET_INDEX = 81;

    private static final String STARTUP_GROUP = "00-startup";

    private static final Pattern EXAMPLE_PATTERN =
            Pattern.compile(".*(?: - )?Example (\\d+)$");

    private TestLogGrouper() {
    }

    public static Map<String, Map<String, List<String>>> group(List<String> lines) {
        Map<String, Map<String, List<String>>> groups = new LinkedHashMap<>();

        String currentTestCase = null;
        String currentExecution = null;

        for (String line : lines) {
            ParsedLogLine parsed = parse(line);

            if (parsed != null) {
                currentTestCase = parsed.testCaseId();
                currentExecution = normalizeExecutionId(
                        parsed.testCaseId(),
                        parsed.testExecutionId()
                );

                line = replaceExecutionId(
                        line,
                        parsed.testExecutionId(),
                        currentExecution
                );
            }

            String testCase = currentTestCase != null
                    ? currentTestCase
                    : STARTUP_GROUP;

            String execution = currentExecution != null
                    ? currentExecution
                    : STARTUP_GROUP;

            groups
                    .computeIfAbsent(testCase, ignored -> new LinkedHashMap<>())
                    .computeIfAbsent(execution, ignored -> new ArrayList<>())
                    .add(line);
        }

        return groups;
    }

    static ParsedLogLine parse(String line) {
        String testCaseId = extractTestCaseId(line);

        if (testCaseId == null) {
            return null;
        }

        String testExecutionId = extractTestExecutionId(line);

        if (testExecutionId == null) {
            return null;
        }

        return new ParsedLogLine(testCaseId, testExecutionId);
    }

    public static String extractTestCaseId(String line) {
        if (line == null || line.length() <= TEST_CASE_CLOSE_BRACKET_INDEX) {
            return null;
        }

        if (line.charAt(TEST_CASE_OPEN_BRACKET_INDEX) != '['
                || line.charAt(TEST_CASE_CLOSE_BRACKET_INDEX) != ']') {
            return null;
        }

        String value = line.substring(
                TEST_CASE_VALUE_INDEX,
                TEST_CASE_VALUE_INDEX + TEST_CASE_WIDTH
        ).stripTrailing();

        return value.isBlank() ? null : value;
    }

    public static String extractTestExecutionId(String line) {
        if (line == null
                || line.length() <= TEST_EXECUTION_OPEN_BRACKET_INDEX
                || line.charAt(TEST_EXECUTION_OPEN_BRACKET_INDEX) != '[') {
            return null;
        }

        int closeBracket = line.indexOf(
                ']',
                TEST_EXECUTION_OPEN_BRACKET_INDEX + 1
        );

        if (closeBracket < 0) {
            return null;
        }

        String value = line.substring(
                TEST_EXECUTION_OPEN_BRACKET_INDEX + 1,
                closeBracket
        );

        return value.isBlank() ? null : value;
    }

    static String normalizeExecutionId(
            String testCaseId,
            String testExecutionId
    ) {
        if (testExecutionId.equals(testCaseId)) {
            return testCaseId;
        }

        String prefix = testCaseId + " - ";

        if (testExecutionId.startsWith(prefix)) {
            return testExecutionId.substring(prefix.length());
        }

        return testExecutionId;
    }

    static String replaceExecutionId(
            String line,
            String originalExecutionId,
            String normalizedExecutionId
    ) {
        if (originalExecutionId.equals(normalizedExecutionId)) {
            return line;
        }

        int start = TEST_EXECUTION_OPEN_BRACKET_INDEX + 1;
        int end = start + originalExecutionId.length();

        return line.substring(0, start)
                + normalizedExecutionId
                + line.substring(end);
    }

    public static int groupFile(Path source, Path target) throws IOException {
        List<String> lines = Files.readAllLines(source);

        Map<String, Map<String, List<String>>> groups = group(lines);

        StringBuilder output = new StringBuilder();

        for (Map.Entry<String, Map<String, List<String>>> testCaseEntry
                : groups.entrySet()) {

            output.append(System.lineSeparator());
            output.append(
                    "========================================================================"
            ).append(System.lineSeparator());

            output.append("  TEST CASE: ")
                    .append(testCaseEntry.getKey())
                    .append(System.lineSeparator());

            output.append(
                    "========================================================================"
            ).append(System.lineSeparator());

            List<Map.Entry<String, List<String>>> executions =
                    new ArrayList<>(testCaseEntry.getValue().entrySet());

            executions.sort(executionComparator());

            for (Map.Entry<String, List<String>> executionEntry : executions) {
                if (shouldPrintExecutionHeader(
                        testCaseEntry.getKey(),
                        executionEntry.getKey(),
                        executions.size()
                )) {
                    output.append(System.lineSeparator());
                    output.append("  EXECUTION: ")
                            .append(executionEntry.getKey())
                            .append(System.lineSeparator());

                    output.append(
                            "------------------------------------------------------------------------"
                    ).append(System.lineSeparator());
                }

                for (String line : executionEntry.getValue()) {
                    output.append(line)
                            .append(System.lineSeparator());
                }
            }
        }

        Path parent = target.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(target, output.toString());

        return groups.size();
    }

    private static Comparator<Map.Entry<String, List<String>>> executionComparator() {
        return Comparator
                .comparingInt(
                        (Map.Entry<String, List<String>> entry) ->
                                exampleNumber(entry.getKey())
                                        .orElse(Integer.MAX_VALUE)
                )
                .thenComparing(Map.Entry::getKey);
    }

    private static java.util.OptionalInt exampleNumber(String executionId) {
        Matcher matcher = EXAMPLE_PATTERN.matcher(executionId);

        if (!matcher.matches()) {
            return java.util.OptionalInt.empty();
        }

        return java.util.OptionalInt.of(
                Integer.parseInt(matcher.group(1))
        );
    }

    private static boolean shouldPrintExecutionHeader(
            String testCaseId,
            String executionId,
            int executionCount
    ) {
        return executionCount > 1
                || !executionId.equals(testCaseId);
    }

    record ParsedLogLine(
            String testCaseId,
            String testExecutionId
    ) {
    }
}