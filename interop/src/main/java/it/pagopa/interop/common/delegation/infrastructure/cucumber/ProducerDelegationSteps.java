package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProducerDelegationSteps {

    private final ProducerDelegationSetupMapper mapper;
    private final InteropJourney interopJourney;
    private final DelegationUseCase delegationUseCase;

    @When("l'utente tenta di revocare {currentProducerDelegation}")
    public void attemptProducerDelegationRevocation(Delegation delegation) {
        delegationUseCase.attemptProducerDelegationRevocation(delegation.getRef());
    }

    @When("il {tenant} tenta di proporre la delega in erogazione a {tenant} per l'{currentEService}")
    public void attemptProducerDelegationCreation(Tenant delegator, Tenant delegate, EService eService) {
        delegationUseCase.attemptProducerDelegationCreation(
                TenantRef.of(delegator.getOrganizationId()), TenantRef.of(delegate.getOrganizationId()), eService);
    }

    @When("l'utente tenta di rifiutare {currentProducerDelegation}")
    public void attemptProducerDelegationRejection(Delegation delegation) {
        delegationUseCase.attemptProducerDelegationRejection(delegation.getRef());
    }

    @Given("l'e-service {string} delegato in erogazione dal {tenant} al {tenant} con delega {producerDelegationState}")
    @Given("l'e-service {string} delegato in erogazione da {tenant} al {tenant} con delega {producerDelegationState}")
    @Given("l'e-service {string} delegato in erogazione dal {tenant} a {tenant} con delega {producerDelegationState}")
    public void setUpProducerDelegation(
            String eServiceAlias,
            Tenant delegator,
            Tenant delegate,
            ProducerDelegationState state
    ) {
        interopJourney.setUpProducerDelegation(
                mapper.toCommand(eServiceAlias, delegator, delegate, state)
        );
    }
}
