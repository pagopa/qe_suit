package it.pagopa.interop.common.risk_analysis.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class RiskAnalysisAnswerTemplate {
    List<String> values;
    Boolean editable;
    List<String> suggestedValues;
}
