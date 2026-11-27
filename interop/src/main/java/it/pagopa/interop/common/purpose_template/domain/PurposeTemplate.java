package it.pagopa.interop.common.purpose_template.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormTemplate;
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
    TenantKind targetTenantKind;
    String title;
    String description;
    String targetDescription;
    Boolean purposeIsFreeOfCharge;
    Integer purposeDailyCalls;
    Boolean handlesPersonalData;
    PurposeTemplateState state;
    Instant createdAt;
    Instant updatedAt;
    RiskAnalysisFormTemplate riskAnalysisFormTemplate;

    @Singular("eserviceId")
    List<UUID> linkedEServiceIds;

    @Singular("resourceId")
    List<UUID> linkedResourceIds;

    @Singular("riskAnalysisAnswerId")
    Map<String, UUID> riskAnalysisAnswerIds;

    @Singular("annotationDocument")
    List<Document> annotationDocuments;

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
