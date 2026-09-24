package it.pagopa.reporting.dto;

import it.pagopa.reporting.dto.annotation.ReportField;
import it.pagopa.reporting.dto.annotation.ReportFieldFormat;
import it.pagopa.reporting.dto.annotation.ReportNode;
import it.pagopa.reporting.dto.annotation.ReportNodeRole;

import java.util.List;

@ReportNode(role = ReportNodeRole.TEST_CLASS, label = "Test class")
public record TestClassReport(
        @ReportField(label = "Class name", order = 10, filterable = true) String className,
        @ReportField(label = "Counts", order = 20) StatusCounts counts,
        @ReportField(label = "Duration (s)", order = 30, format = ReportFieldFormat.DURATION_SECONDS) double totalDurationSeconds,
        @ReportField(label = "Factories", order = 40) List<TestFactoryReport> factories
) {
    public TestClassReport {
        factories = List.copyOf(factories);
    }
}

