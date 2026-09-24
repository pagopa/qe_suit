package it.pagopa.reporting.dto;

import it.pagopa.reporting.dto.annotation.ReportField;
import it.pagopa.reporting.dto.annotation.ReportFieldFormat;
import it.pagopa.reporting.dto.annotation.ReportNode;
import it.pagopa.reporting.dto.annotation.ReportNodeRole;

@ReportNode(role = ReportNodeRole.CONCRETE_CASE, label = "Concrete runtime case")
public record ConcreteCaseReport(
        @ReportField(label = "Case ID", order = 5, filterable = true) String id,
        @ReportField(label = "Display name", order = 10, filterable = true) String displayName,
        @ReportField(label = "Scenario", order = 20, filterable = true) String scenario,
        @ReportField(label = "Target path", order = 30, filterable = true) String targetPath,
        @ReportField(label = "Status", order = 40, format = ReportFieldFormat.STATUS, filterable = true) ExecutionStatus status,
        @ReportField(label = "Source testcase", order = 50, filterable = true) String sourceTestcaseName,
        @ReportField(label = "Source index", order = 60, format = ReportFieldFormat.INTEGER) Integer sourceIndex,
        @ReportField(label = "Duration (s)", order = 70, format = ReportFieldFormat.DURATION_SECONDS) double durationSeconds,
        @ReportField(label = "Failure type", order = 80) String failureType,
        @ReportField(label = "Failure message", order = 90, format = ReportFieldFormat.MULTILINE) String failureMessage,
        @ReportField(label = "Stacktrace", order = 100, format = ReportFieldFormat.CODE_BLOCK) String stackTrace,
        @ReportField(label = "Case log", order = 110, format = ReportFieldFormat.CODE_BLOCK) String caseLog
) {
    public String searchableText() {
        StringBuilder builder = new StringBuilder();
        appendIfPresent(builder, displayName);
        appendIfPresent(builder, scenario);
        appendIfPresent(builder, targetPath);
        appendIfPresent(builder, sourceTestcaseName);
        appendIfPresent(builder, failureMessage);
        return builder.toString().trim().toLowerCase();
    }

    private static void appendIfPresent(StringBuilder builder, String text) {
        if (text != null && !text.isBlank()) {
            builder.append(text).append(' ');
        }
    }
}

