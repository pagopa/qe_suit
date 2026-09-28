package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserJourneySteps {

    private final InteropJourney interopJourney;

    @Given("un erogatore {userRole} del {tenant}")
    public void setProducer(UserRole userRole, Tenant producer) {
        interopJourney.withProducer(producer, userRole);
    }

    @Given("un fruitore {userRole} del {tenant}")
    public void setConsumer(UserRole userRole, Tenant consumer) {
        interopJourney.withConsumer(consumer, userRole);
    }
}
