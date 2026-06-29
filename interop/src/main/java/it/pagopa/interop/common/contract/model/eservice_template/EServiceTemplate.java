package it.pagopa.interop.common.contract.model.eservice_template;

import it.pagopa.interop.common.contract.model.eservice.EServiceMode;
import it.pagopa.interop.common.contract.model.eservice.EServiceTechnology;
import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysis;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class EServiceTemplate implements TestModel {
    UUID id;
    UUID creatorId;
    String name;
    String description;
    String intendedTarget;
    EServiceTechnology technology;
    EServiceMode mode;
    EServiceTemplateState state;
    Boolean personalData;
    Instant createdAt;
    Instant updatedAt;

    @Singular("version")
    List<EServiceTemplateVersion> versions;

    @Singular("riskAnalysis")
    List<RiskAnalysis> riskAnalyses;
}
