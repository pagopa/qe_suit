package it.pagopa.interop.common.agreement.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.Document;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
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
    List<Document> documents;

    public AgreementRef getRef(){
        return new AgreementRef(id);
    }
}
