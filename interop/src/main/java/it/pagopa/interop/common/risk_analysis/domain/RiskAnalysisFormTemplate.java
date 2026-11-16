package it.pagopa.interop.common.risk_analysis.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class RiskAnalysisFormTemplate {
    String version;
    Map<String, RiskAnalysisAnswerTemplate> answerTemplates;
}
