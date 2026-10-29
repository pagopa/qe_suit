package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand;
import it.pagopa.interop.common.delegation.domain.DelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DelegationJourneySteps {

    private final InteropJourney interopJourney;

    @Given("l'{currentEService} \u00e8 delegato in erogazione da/dal {tenant} a/al {tenant} con una delega in stato {delegationState}")
    public void createProducerDelegation(
            EService eService, Tenant delegator, Tenant delegate, DelegationState delegationState) {
        interopJourney.createProducerDelegation(ProducerDelegationCreationCommand.builder()
                .delegator(delegator)
                .delegate(delegate)
                .eServiceRef(eService.getRef())
                .targetState(delegationState)
                .build());
    }
}
