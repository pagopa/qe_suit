package it.pagopa.interop.common.purpose_template.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class PurposeTemplate implements Identifiable {
    UUID id;
    UUID creatorId;
    String title;
    String description;
    PurposeTemplateState state;
    Instant createdAt;
    Instant updatedAt;

    @Singular("eserviceId")
    List<UUID> linkedEServiceIds;

    @Singular("resourceId")
    List<UUID> linkedResourceIds;

    @Singular("riskAnalysisAnswerId")
    Map<String, UUID> riskAnalysisAnswerIds;

    public PurposeTemplateRef getRef() {
        return PurposeTemplateRef.of(this.id);
    }

    public UUID getFirstRiskAnalysisAnswerId() {
        if (riskAnalysisAnswerIds == null || riskAnalysisAnswerIds.isEmpty()) {
            throw new IllegalStateException("No risk analysis answers available on PurposeTemplate " + id);
        }
        return riskAnalysisAnswerIds.values().iterator().next();
    }
}
