package it.pagopa.interop.common.delegation.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProducerDelegation implements Identifiable {
    UUID id;
    UUID delegatorId;
    UUID delegateId;
    UUID eServiceId;
    ProducerDelegationState state;

    public DelegationRef getRef() {
        return DelegationRef.of(id);
    }
}
