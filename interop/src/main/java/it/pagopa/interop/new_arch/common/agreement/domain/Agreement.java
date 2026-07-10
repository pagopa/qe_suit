package it.pagopa.interop.new_arch.common.agreement.domain;

import it.pagopa.interop.new_arch.common.kernel.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Agreement implements Identifiable {
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

    public AgreementRef getRef(){
        return new AgreementRef(id);
    }
}