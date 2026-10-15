package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DelegationJourneySteps {

    private final InteropJourney interopJourney;

    @Given("l'{currentEService} \u00e8 delegato in erogazione da/dal {tenant} a/al {tenant} con una delega attiva")
    public void createActiveProducerDelegation(EService eService, Tenant delegator, Tenant delegate) {
        interopJourney.createActiveProducerDelegation(delegator, delegate, eService.getRef());
    }
}
