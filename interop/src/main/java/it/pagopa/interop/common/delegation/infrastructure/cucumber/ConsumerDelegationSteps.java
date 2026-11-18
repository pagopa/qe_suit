package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsumerDelegationSteps {
    private final DelegationUseCase delegationUseCase;

    @When("l'utente tenta di approvare {currentConsumerDelegation}")
    public void attemptConsumerDelegationApproval(Delegation delegation) {
        delegationUseCase.attemptConsumerDelegationApproval(delegation.getRef());
    }

    @When("l'utente tenta di revocare {currentConsumerDelegation}")
    public void attemptConsumerDelegationRevocation(Delegation delegation) {
        delegationUseCase.attemptConsumerDelegationRevocation(delegation.getRef());
    }

    @When("l'utente tenta di rifiutare {currentConsumerDelegation}")
    public void attemptConsumerDelegationRejection(Delegation delegation) {
        delegationUseCase.attemptConsumerDelegationRejection(delegation.getRef());
    }

    @When("{tenant} tenta di proporre la delega in fruizione a {tenant} per l'{currentEService}")
    public void attemptConsumerDelegationCreation(Tenant delegator, Tenant delegate, EService eService) {
        delegationUseCase.attemptConsumerDelegationCreation(
                TenantRef.of(delegator.getOrganizationId()), TenantRef.of(delegate.getOrganizationId()), eService);
    }

    @When("l'utente tenta di rifiutare {currentConsumerDelegation} inserendo la motivazione {string}")
    public void attemptConsumerDelegationRejection(Delegation delegation, String rejectionReason) {
        delegationUseCase.attemptConsumerDelegationRejection(delegation.getRef(), rejectionReason);
    }
}
