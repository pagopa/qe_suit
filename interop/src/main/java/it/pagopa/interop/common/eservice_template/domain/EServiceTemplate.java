package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.*;

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
    @NonFinal
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

    public EServiceTemplateVersion findVersion(UUID versionId) {
        return versions.stream()
                .filter(version -> version.getId().equals(versionId))
                .findFirst()
                .orElse(null);
    }

    public EServiceTemplateVersion getLastDraftVersion() {
        return getUniqueVersionByState(EServiceTemplateVersionState.DRAFT);
    }

    public EServiceTemplateVersion getLastPublishedVersion() {
        return getUniqueVersionByState(EServiceTemplateVersionState.PUBLISHED);
    }

    public void addVersion(EServiceTemplateVersion version) {
        Objects.requireNonNull(version, "version cannot be null");
        Objects.requireNonNull(version.getId(), "version.id cannot be null");
        Objects.requireNonNull(versions, "versions cannot be null");

        List<EServiceTemplateVersion> updatedVersions = new ArrayList<>(versions);
        for (int i = 0; i < updatedVersions.size(); i++) {
            EServiceTemplateVersion current = updatedVersions.get(i);
            if (current.getId().equals(version.getId())) {
                updatedVersions.set(i, version);
                versions = updatedVersions;
                return;
            }
        }

        updatedVersions.add(version);
        versions = updatedVersions;
    }

    private EServiceTemplateVersion getUniqueVersionByState(EServiceTemplateVersionState state) {
        List<EServiceTemplateVersion> matchingVersions = versions.stream()
                .filter(version -> version.getState() == state)
                .toList();

        if (matchingVersions.isEmpty()) {
            throw new NoSuchElementException(
                    "No version in state " + state + " found for EServiceTemplate " + id
            );
        }

        if (matchingVersions.size() > 1) {
            throw new IllegalStateException(
                    "Found " + matchingVersions.size()
                            + " versions in state " + state
                            + " for EServiceTemplate " + id
                            + ", expected exactly 1"
            );
        }

        return matchingVersions.get(0);
    }
}
