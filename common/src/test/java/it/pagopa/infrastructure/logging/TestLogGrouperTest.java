package it.pagopa.infrastructure.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestLogGrouperTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldExtractTestCaseId() {
        String line = logLine(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1",
                "message"
        );

        assertEquals(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                TestLogGrouper.extractTestCaseId(line)
        );
    }

    @Test
    void shouldExtractTestExecutionId() {
        String line = logLine(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1",
                "message"
        );

        assertEquals(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1",
                TestLogGrouper.extractTestExecutionId(line)
        );
    }

    @Test
    void shouldReturnNullForNonLogLine() {
        assertNull(
                TestLogGrouper.extractTestCaseId(
                        "java.lang.AssertionError: boom"
                )
        );

        assertNull(
                TestLogGrouper.extractTestExecutionId(
                        "java.lang.AssertionError: boom"
                )
        );
    }

    @Test
    void shouldNormalizeExecutionIdRemovingTestCasePrefix() {
        assertEquals(
                "Example 1",
                TestLogGrouper.normalizeExecutionId(
                        "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                        "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1"
                )
        );
    }

    @Test
    void shouldKeepExecutionIdWhenItDoesNotContainTestCasePrefix() {
        assertEquals(
                "[pathParams] REPLACED_WITH_MALFORMED_UUID @ /agreementId",
                TestLogGrouper.normalizeExecutionId(
                        "getAgreementById",
                        "[pathParams] REPLACED_WITH_MALFORMED_UUID @ /agreementId"
                )
        );
    }

    @Test
    void shouldKeepTestCaseIdWhenExecutionEqualsTestCaseId() {
        assertEquals(
                "createAgreement",
                TestLogGrouper.normalizeExecutionId(
                        "createAgreement",
                        "createAgreement"
                )
        );
    }

    @Test
    void shouldGroupDifferentExamplesUnderSameTestCase() {
        String example1 = logLine(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1",
                "message example 1"
        );

        String example2 = logLine(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 2",
                "message example 2"
        );

        Map<String, Map<String, List<String>>> groups =
                TestLogGrouper.group(List.of(example2, example1));

        assertEquals(1, groups.size());

        Map<String, List<String>> executions =
                groups.get("DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3");

        assertEquals(2, executions.size());
        assertTrue(executions.containsKey("Example 1"));
        assertTrue(executions.containsKey("Example 2"));
    }

    @Test
    void shouldRemoveRedundantTestCasePrefixFromGroupedLines() {
        String line = logLine(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1",
                "message"
        );

        Map<String, Map<String, List<String>>> groups =
                TestLogGrouper.group(List.of(line));

        String groupedLine = groups
                .get("DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3")
                .get("Example 1")
                .get(0);

        assertTrue(groupedLine.contains("[Example 1]"));

        assertFalse(
                groupedLine.contains(
                        "[DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1]"
                )
        );
    }

    @Test
    void shouldAttachMultilineRowsToCurrentExecution() {
        String firstLine = logLine(
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1",
                "Response Body: <html>"
        );

        Map<String, Map<String, List<String>>> groups =
                TestLogGrouper.group(
                        List.of(
                                firstLine,
                                "<head><title>503</title></head>",
                                "<body>",
                                "<h1>Unavailable</h1>",
                                "</body>",
                                "</html>"
                        )
                );

        List<String> executionLines = groups
                .get("DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3")
                .get("Example 1");

        assertEquals(6, executionLines.size());
        assertEquals(
                "<head><title>503</title></head>",
                executionLines.get(1)
        );
        assertEquals("</html>", executionLines.get(5));
    }

    @Test
    void shouldPutInitialUnformattedLinesIntoStartupGroup() {
        String testLine = logLine(
                "TEST_CASE",
                "TEST_CASE",
                "message"
        );

        Map<String, Map<String, List<String>>> groups =
                TestLogGrouper.group(
                        List.of(
                                "OpenJDK warning",
                                "some startup output",
                                testLine
                        )
                );

        assertTrue(groups.containsKey("00-startup"));
        assertTrue(groups.containsKey("TEST_CASE"));

        List<String> startupLines = groups
                .get("00-startup")
                .get("00-startup");

        assertEquals(
                List.of(
                        "OpenJDK warning",
                        "some startup output"
                ),
                startupLines
        );
    }

    @Test
    void shouldOrderExamplesNumericallyInGeneratedFile() throws Exception {
        Path source = tempDir.resolve("qa-test.log");
        Path target = tempDir.resolve("qa-test.by-test-case.log");

        Files.write(
                source,
                List.of(
                        logLine(
                                "TEST_CASE",
                                "TEST_CASE - Example 10",
                                "message 10"
                        ),
                        logLine(
                                "TEST_CASE",
                                "TEST_CASE - Example 2",
                                "message 2"
                        ),
                        logLine(
                                "TEST_CASE",
                                "TEST_CASE - Example 1",
                                "message 1"
                        )
                )
        );

        int groups = TestLogGrouper.groupFile(source, target);

        assertEquals(1, groups);

        String output = Files.readString(target);

        int example1 = output.indexOf("EXECUTION: Example 1");
        int example2 = output.indexOf("EXECUTION: Example 2");
        int example10 = output.indexOf("EXECUTION: Example 10");

        assertTrue(example1 >= 0);
        assertTrue(example2 >= 0);
        assertTrue(example10 >= 0);

        assertTrue(example1 < example2);
        assertTrue(example2 < example10);
    }

    @Test
    void shouldGenerateSeparateExecutionSections() throws Exception {
        Path source = tempDir.resolve("qa-test.log");
        Path target = tempDir.resolve("qa-test.by-test-case.log");

        Files.write(
                source,
                List.of(
                        logLine(
                                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 2",
                                "second"
                        ),
                        logLine(
                                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3",
                                "DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1",
                                "first"
                        )
                )
        );

        TestLogGrouper.groupFile(source, target);

        String output = Files.readString(target);

        assertTrue(
                output.contains(
                        "TEST CASE: DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3"
                )
        );

        assertTrue(output.contains("EXECUTION: Example 1"));
        assertTrue(output.contains("EXECUTION: Example 2"));

        assertFalse(
                output.contains(
                        "[DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 1]"
                )
        );

        assertFalse(
                output.contains(
                        "[DEBUG_INTEROP_CLIENT_ASSERTION_DPOP_3 - Example 2]"
                )
        );

        assertTrue(output.contains("[Example 1]"));
        assertTrue(output.contains("[Example 2]"));
    }

    @Test
    void shouldNotPrintExecutionHeaderForSimpleTest() throws Exception {
        Path source = tempDir.resolve("qa-test.log");
        Path target = tempDir.resolve("qa-test.by-test-case.log");

        Files.write(
                source,
                List.of(
                        logLine(
                                "createAgreement",
                                "createAgreement",
                                "message one"
                        ),
                        logLine(
                                "createAgreement",
                                "createAgreement",
                                "message two"
                        )
                )
        );

        TestLogGrouper.groupFile(source, target);

        String output = Files.readString(target);

        assertTrue(output.contains("TEST CASE: createAgreement"));
        assertFalse(output.contains("EXECUTION: createAgreement"));
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