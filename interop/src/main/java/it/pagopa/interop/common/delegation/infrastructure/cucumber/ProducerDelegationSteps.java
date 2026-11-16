package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProducerDelegationSteps {

    private final ProducerDelegationSetupMapper mapper;
    private final InteropJourney interopJourney;

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
