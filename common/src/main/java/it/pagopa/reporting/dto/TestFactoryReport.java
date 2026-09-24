package it.pagopa.reporting.dto;

import it.pagopa.reporting.dto.annotation.ReportField;
import it.pagopa.reporting.dto.annotation.ReportFieldFormat;
import it.pagopa.reporting.dto.annotation.ReportNode;
import it.pagopa.reporting.dto.annotation.ReportNodeRole;

import java.util.List;

@ReportNode(role = ReportNodeRole.TEST_FACTORY, label = "Test factory")
public record TestFactoryReport(
        @ReportField(label = "Factory name", order = 10, filterable = true) String factoryName,
        @ReportField(label = "Counts", order = 20) StatusCounts counts,
        @ReportField(label = "Duration (s)", order = 30, format = ReportFieldFormat.DURATION_SECONDS) double totalDurationSeconds,
        @ReportField(label = "Concrete cases", order = 40) List<ConcreteCaseReport> concreteCases
) {
    public TestFactoryReport {
        concreteCases = List.copyOf(concreteCases);
    }
}

