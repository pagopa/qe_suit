package it.pagopa.reporting.dto;

import it.pagopa.reporting.dto.annotation.ReportField;
import it.pagopa.reporting.dto.annotation.ReportFieldFormat;
import it.pagopa.reporting.dto.annotation.ReportNode;
import it.pagopa.reporting.dto.annotation.ReportNodeRole;

@ReportNode(role = ReportNodeRole.SUMMARY, label = "Status counts")
public record StatusCounts(
        @ReportField(label = "Total", order = 10, format = ReportFieldFormat.INTEGER) long total,
        @ReportField(label = "Passed", order = 20, format = ReportFieldFormat.INTEGER) long passed,
        @ReportField(label = "Failed", order = 30, format = ReportFieldFormat.INTEGER) long failed,
        @ReportField(label = "Errors", order = 40, format = ReportFieldFormat.INTEGER) long errors,
        @ReportField(label = "Skipped", order = 50, format = ReportFieldFormat.INTEGER) long skipped,
        @ReportField(label = "Unknown", order = 60, format = ReportFieldFormat.INTEGER) long unknown
) {
    public static StatusCounts empty() {
        return new StatusCounts(0, 0, 0, 0, 0, 0);
    }

    public StatusCounts plus(StatusCounts other) {
        return new StatusCounts(
                total + other.total,
                passed + other.passed,
                failed + other.failed,
                errors + other.errors,
                skipped + other.skipped,
                unknown + other.unknown
        );
    }

    public static StatusCounts fromBuilder(Builder builder) {
        return new StatusCounts(
                builder.total,
                builder.passed,
                builder.failed,
                builder.errors,
                builder.skipped,
                builder.unknown
        );
    }

    public static final class Builder {
        private long total;
        private long passed;
        private long failed;
        private long errors;
        private long skipped;
        private long unknown;

        public void add(ExecutionStatus status) {
            total++;
            if (status == null) {
                unknown++;
                return;
            }

            switch (status) {
                case PASSED -> passed++;
                case FAILED -> failed++;
                case ERROR -> errors++;
                case SKIPPED -> skipped++;
                case UNKNOWN -> unknown++;
            }
        }

        public StatusCounts build() {
            return StatusCounts.fromBuilder(this);
        }
    }
}

