package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.delegation.domain.Delegation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsumerDelegationSteps {
    private final DelegationUseCase delegationUseCase;

    @When("l'utente tenta di rifiutare {currentConsumerDelegation}")
    public void attemptConsumerDelegationRejection(Delegation delegation) {
        delegationUseCase.attemptConsumerDelegationRejection(delegation.getRef());
    }
}
