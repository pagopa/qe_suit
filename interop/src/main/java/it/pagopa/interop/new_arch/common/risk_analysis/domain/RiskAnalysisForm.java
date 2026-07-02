package it.pagopa.interop.new_arch.common.risk_analysis.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class RiskAnalysisForm {
    String version;
    Map<String, List<String>> answers;
}
