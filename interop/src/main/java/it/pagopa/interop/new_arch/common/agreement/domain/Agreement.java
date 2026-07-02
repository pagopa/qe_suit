package it.pagopa.interop.new_arch.common.agreement.domain;

import it.pagopa.interop.common.contract.model.TestModel;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

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
    UUID delegationId;
    Boolean suspendedByConsumer;
    Boolean suspendedByProducer;
    String rejectionReason;
}