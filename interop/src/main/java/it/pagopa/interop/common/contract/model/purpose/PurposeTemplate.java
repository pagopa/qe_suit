package it.pagopa.interop.common.contract.model.purpose;

import it.pagopa.interop.common.contract.model.Identifiable;
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
}
