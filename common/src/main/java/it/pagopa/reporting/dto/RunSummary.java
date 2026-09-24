package it.pagopa.reporting.dto;

import it.pagopa.reporting.dto.annotation.ReportField;
import it.pagopa.reporting.dto.annotation.ReportFieldFormat;
import it.pagopa.reporting.dto.annotation.ReportNode;
import it.pagopa.reporting.dto.annotation.ReportNodeRole;

@ReportNode(role = ReportNodeRole.SUMMARY, label = "Run summary")
public record RunSummary(
        @ReportField(label = "Tests", order = 10, format = ReportFieldFormat.INTEGER) long tests,
        @ReportField(label = "Failures", order = 20, format = ReportFieldFormat.INTEGER) long failures,
        @ReportField(label = "Errors", order = 30, format = ReportFieldFormat.INTEGER) long errors,
        @ReportField(label = "Skipped", order = 40, format = ReportFieldFormat.INTEGER) long skipped,
        @ReportField(label = "Duration (s)", order = 50, format = ReportFieldFormat.DURATION_SECONDS) double durationSeconds
) {
    public long passed() {
        long passed = tests - failures - errors - skipped;
        return Math.max(passed, 0);
    }
}

