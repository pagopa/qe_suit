package it.pagopa.interop.common.contract.model.risk_analysis;

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
