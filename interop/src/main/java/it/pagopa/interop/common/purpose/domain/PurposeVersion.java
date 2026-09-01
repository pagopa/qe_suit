package it.pagopa.interop.common.purpose.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.PurposeVersionRef;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class PurposeVersion implements Identifiable {
    UUID id;
    PurposeVersionState purposeVersionState;
    Integer dailyCalls;
    Instant createdAt;
    Instant updatedAt;
    Instant suspendedAt;

    public PurposeVersionRef getRef() {
        return PurposeVersionRef.of(id);
    }
}
