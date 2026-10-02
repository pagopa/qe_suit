package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.domain.Identifiable;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
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

    public EServiceTemplateRef getRef() {
        return new EServiceTemplateRef(id);
    }

    public EServiceTemplateVersion findVersion(UUID versionId) {
        return versions.stream()
                .filter(version -> version.getId().equals(versionId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Nessuna versione " + versionId + " trovata per EServiceTemplate " + id));
    }

    public EServiceTemplateVersion lastVersion() {
        if (versions.isEmpty()) {
            throw new NoSuchElementException("Nessuna versione trovata per EServiceTemplate " + id);
        }
        return versions.get(versions.size() - 1);
    }
}
