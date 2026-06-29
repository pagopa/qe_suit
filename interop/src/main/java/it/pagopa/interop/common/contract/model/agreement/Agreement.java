package it.pagopa.interop.common.contract.model.agreement;

import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.shared.DelegationRef;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Agreement implements TestModel {
    UUID id;
    UUID eserviceId;
    UUID descriptorId;
    UUID producerId;
    UUID consumerId;
    AgreementState state;
    DelegationRef delegationRef;
    Boolean suspendedByConsumer;
    Boolean suspendedByProducer;
    String rejectionReason;
    Instant createdAt;
    Instant updatedAt;
    Instant submittedAt;
    Instant activatedAt;
}