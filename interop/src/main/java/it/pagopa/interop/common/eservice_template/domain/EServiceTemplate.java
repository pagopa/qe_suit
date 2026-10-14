package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.Comparator;
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

    public EServiceTemplateVersion getLatestVersion() {
        return versions.stream()
                .max(Comparator.comparingInt(version -> Integer.parseInt(version.getVersion())))
                .orElseThrow(() -> new NoSuchElementException("Nessuna versione trovata per template " + id));
    }

    public EServiceTemplateRef getRef() {
        return new EServiceTemplateRef(id);
    }

    public EServiceTemplateVersion getVersion(int versionNumber) {
        return versions.stream()
                .filter(version -> Integer.parseInt(version.getVersion()) == versionNumber)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Versione " + versionNumber + " non trovata per template " + id
                ));
    }
}
