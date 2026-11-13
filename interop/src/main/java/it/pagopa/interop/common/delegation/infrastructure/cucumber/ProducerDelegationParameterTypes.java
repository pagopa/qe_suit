package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProducerDelegationParameterTypes {

    private final EntityStore entityStore;

    @ParameterType("la delega in erogazione")
    public Delegation currentProducerDelegation(String reference) {
        return entityStore.getLastOrThrow(Delegation.class);
    }

    @ParameterType("attiva|in attesa di accettazione|rifiutata|revocata")
    public ProducerDelegationState producerDelegationState(String state) {
        return switch (state) {
            case "attiva" -> ProducerDelegationState.ACTIVE;
            case "in attesa di accettazione" -> ProducerDelegationState.WAITING_FOR_APPROVAL;
            case "rifiutata" -> ProducerDelegationState.REJECTED;
            case "revocata" -> ProducerDelegationState.REVOKED;
            default -> throw new IllegalArgumentException("Stato delega di erogazione non valido: " + state);
        };
    }
}
