package it.pagopa.infrastructure.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestLogGrouperTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldExtractTestCaseId() {
        String line =
                "2026-09-29 01:32:00.935 INFO  "
                        + "[DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3           ] "
                        + "[DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1] "
                        + "[ForkJoinPool-2-worker-8] logger - message";

        assertEquals(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                TestLogGrouper.extractTestCaseId(line)
        );
    }

    @Test
    void shouldReturnNullForNonFormattedLine() {
        assertNull(
                TestLogGrouper.extractTestCaseId(
                        "java.lang.AssertionError: boom"
                )
        );
    }

    @Test
    void shouldReturnNullForNullLine() {
        assertNull(TestLogGrouper.extractTestCaseId(null));
    }

    @Test
    void shouldAttachMultilineLinesToPreviousTestCase() {
        String firstLine = logLine(
                "TEST_CASE",
                "execution",
                "Response Body:"
        );

        Map<String, StringBuilder> groups = TestLogGrouper.group(
                List.of(
                        firstLine,
                        "<html>",
                        "<body>",
                        "boom",
                        "</body>",
                        "</html>"
                )
        );

        assertEquals(1, groups.size());

        String grouped = groups.get("TEST_CASE").toString();

        assertTrue(grouped.contains("Response Body:"));
        assertTrue(grouped.contains("<html>"));
        assertTrue(grouped.contains("boom"));
        assertTrue(grouped.contains("</html>"));
    }

    @Test
    void shouldPreserveOrderOfFirstAppearance() {
        String first = logLine(
                "FIRST",
                "execution-1",
                "first"
        );

        String second = logLine(
                "SECOND",
                "execution-2",
                "second"
        );

        String firstAgain = logLine(
                "FIRST",
                "execution-1",
                "third"
        );

        Map<String, StringBuilder> groups = TestLogGrouper.group(
                List.of(
                        first,
                        second,
                        firstAgain
                )
        );

        assertEquals(
                List.of("FIRST", "SECOND"),
                groups.keySet().stream().toList()
        );
    }

    @Test
    void shouldGroupSameTestCaseWithDifferentExecutions() {
        String exampleOne = logLine(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1",
                "first execution"
        );

        String exampleFour = logLine(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 4",
                "second execution"
        );

        Map<String, StringBuilder> groups = TestLogGrouper.group(
                List.of(
                        exampleOne,
                        exampleFour
                )
        );

        assertEquals(1, groups.size());

        String grouped = groups
                .get("DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3")
                .toString();

        assertTrue(grouped.contains("Example 1"));
        assertTrue(grouped.contains("Example 4"));
    }

    @Test
    void shouldPutInitialUnformattedLinesInStartupGroup() {
        String testLine = logLine(
                "TEST_CASE",
                "execution",
                "message"
        );

        Map<String, StringBuilder> groups = TestLogGrouper.group(
                List.of(
                        "JVM startup message",
                        "another startup message",
                        testLine
                )
        );

        assertEquals(
                List.of(
                        "00-startup",
                        "TEST_CASE"
                ),
                groups.keySet().stream().toList()
        );

        assertTrue(
                groups.get("00-startup")
                        .toString()
                        .contains("JVM startup message")
        );
    }

    @Test
    void shouldCreateGroupedFile() throws Exception {
        Path source = tempDir.resolve("qa-test.log");
        Path target = tempDir.resolve("qa-test.by-test-case.log");

        Files.write(
                source,
                List.of(
                        logLine(
                                "FIRST",
                                "execution-1",
                                "message one"
                        ),
                        "continuation line",
                        logLine(
                                "SECOND",
                                "execution-2",
                                "message two"
                        )
                )
        );

        int groups = TestLogGrouper.groupFile(source, target);

        assertEquals(2, groups);
        assertTrue(Files.exists(target));

        String output = Files.readString(target);

        assertTrue(output.contains("TEST CASE: FIRST"));
        assertTrue(output.contains("TEST CASE: SECOND"));
        assertTrue(output.contains("continuation line"));
    }

    private static String logLine(
            String testCaseId,
            String testExecutionId,
            String message
    ) {
        return String.format(
                "2026-09-29 01:32:00.935 INFO  [%-48.48s] [%s] [main] logger - %s",
                testCaseId,
                testExecutionId,
                message
        );
    }
}