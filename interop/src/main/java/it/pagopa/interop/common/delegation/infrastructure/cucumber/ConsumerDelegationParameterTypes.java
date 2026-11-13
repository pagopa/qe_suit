package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.delegation.domain.Delegation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsumerDelegationParameterTypes {

    private final EntityStore entityStore;

    @ParameterType("la delega in fruizione")
    public Delegation currentConsumerDelegation(String reference) {
        return entityStore.getLastOrThrow(Delegation.class);
    }
}
