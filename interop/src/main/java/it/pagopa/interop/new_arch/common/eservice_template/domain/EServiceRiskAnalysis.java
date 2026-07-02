package it.pagopa.interop.new_arch.common.eservice_template.domain;

import it.pagopa.interop.common.contract.model.Identifiable;
import it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisForm;
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
