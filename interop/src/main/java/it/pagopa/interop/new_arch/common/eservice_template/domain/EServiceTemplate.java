package it.pagopa.interop.new_arch.common.eservice_template.domain;

import it.pagopa.interop.new_arch.common.eservice.domain.EServiceMode;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.new_arch.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
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
public class EServiceTemplate implements Identifiable {
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
    List<EServiceRiskAnalysis> riskAnalyses;
}
