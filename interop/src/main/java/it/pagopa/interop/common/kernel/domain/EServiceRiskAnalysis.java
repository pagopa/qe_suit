package it.pagopa.interop.common.kernel.domain;

import it.pagopa.kernel.domain.Identifiable;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
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
