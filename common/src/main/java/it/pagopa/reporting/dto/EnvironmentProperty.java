package it.pagopa.reporting.dto;

import it.pagopa.reporting.dto.annotation.ReportField;
import it.pagopa.reporting.dto.annotation.ReportNode;
import it.pagopa.reporting.dto.annotation.ReportNodeRole;

@ReportNode(role = ReportNodeRole.PROPERTY, label = "Environment property")
public record EnvironmentProperty(
        @ReportField(label = "Key", order = 10, filterable = true) String key,
        @ReportField(label = "Value", order = 20, filterable = true) String value
) {
}

