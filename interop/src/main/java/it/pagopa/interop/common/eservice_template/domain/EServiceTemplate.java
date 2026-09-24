package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.domain.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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

    List<EServiceTemplateVersion> versions;

    List<EServiceRiskAnalysis> riskAnalyses;

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

        for (int i = 0; i < versions.size(); i++) {
            EServiceTemplateVersion current = versions.get(i);
            if (current.getId().equals(version.getId())) {
                versions.set(i, version);
                return;
            }
        }

        versions.add(version);
    }

    public EServiceTemplateRef getRef() {
        return EServiceTemplateRef.of(this.id);
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
