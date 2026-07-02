package it.pagopa.interop.new_arch.common.purpose.domain;

import it.pagopa.interop.common.contract.model.TestModel;
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
public class Purpose implements TestModel {
    UUID id;
    UUID eserviceId;
    UUID consumerId;
    Boolean suspendedByConsumer;
    Boolean suspendedByProducer;
    String title;
    String description;
    Instant createdAt;
    Instant updatedAt;
    Boolean isFreeOfCharge;
    UUID delegationId;

    @Singular("version")
    List<PurposeVersion> versions;
}
