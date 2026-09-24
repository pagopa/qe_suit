package it.pagopa.reporting.dto;

import it.pagopa.reporting.dto.annotation.ReportField;
import it.pagopa.reporting.dto.annotation.ReportNode;
import it.pagopa.reporting.dto.annotation.ReportNodeRole;

import java.util.List;

@ReportNode(role = ReportNodeRole.RUN, label = "Report document")
public record ReportDocument(
        @ReportField(label = "Summary", order = 10) RunSummary summary,
        @ReportField(label = "Environment properties", order = 20) List<EnvironmentProperty> environmentProperties,
        @ReportField(label = "Excluded environment property keys", order = 30) List<String> excludedEnvironmentPropertyKeys,
        @ReportField(label = "Test classes", order = 40) List<TestClassReport> testClasses,
        @ReportField(label = "Warnings", order = 50) List<String> warnings
) {
    public ReportDocument {
        environmentProperties = List.copyOf(environmentProperties);
        excludedEnvironmentPropertyKeys = List.copyOf(excludedEnvironmentPropertyKeys);
        testClasses = List.copyOf(testClasses);
        warnings = List.copyOf(warnings);
    }
}

