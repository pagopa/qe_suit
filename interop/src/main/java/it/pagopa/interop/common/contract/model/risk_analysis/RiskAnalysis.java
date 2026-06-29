package it.pagopa.interop.common.contract.model.risk_analysis;

import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Delegate;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class RiskAnalysis implements TestModel {
    UUID id;
    String name;
    String riskAnalysisVersion;
    Instant createdAt;
    Instant rulesetExpiration;
}
