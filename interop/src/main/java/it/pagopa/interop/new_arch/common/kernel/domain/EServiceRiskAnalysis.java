package it.pagopa.interop.new_arch.common.kernel.domain;

import it.pagopa.interop.new_arch.common.risk_analysis.domain.RiskAnalysisForm;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class EServiceRiskAnalysis implements Identifiable {
    UUID id;
    String name;
    RiskAnalysisForm riskAnalysisForm;
}
