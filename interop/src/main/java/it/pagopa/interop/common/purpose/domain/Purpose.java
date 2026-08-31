package it.pagopa.interop.common.purpose.domain;

import it.pagopa.interop.common.kernel.Identifiable;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.pagopa.interop.common.purpose.domain.PurposeVersionState.DRAFT;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Purpose implements Identifiable {
    UUID id;
    UUID eserviceId;
    UUID consumerId;
    Boolean suspendedByConsumer;
    Boolean suspendedByProducer;
    String title;
    String description;
    Boolean isFreeOfCharge;
    UUID delegationId;

    @Singular("version")
    List<PurposeVersion> versions;

    public PurposeRef getRef() {
        return PurposeRef.of(this.id);
    }

    public PurposeVersion getLastDraftVersion() {
        return versions.stream().filter(version -> version.getPurposeVersionState() == DRAFT)
                .max(Comparator.comparing(PurposeVersion::getCreatedAt))
                .orElse(null);
    }

    public PurposeVersion getLastActiveVersion() {
        return versions.stream().filter(version -> version.getPurposeVersionState() == PurposeVersionState.ACTIVE)
                .max(Comparator.comparing(PurposeVersion::getCreatedAt))
                .orElse(null);
    }

    public Optional<PurposeVersion> findVersionById(UUID versionId) {
        return versions.stream()
                .filter(version -> version.getId().equals(versionId))
                .findFirst();
    }
}
