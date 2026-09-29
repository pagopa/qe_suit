package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserJourneySteps {

    private final InteropJourney interopJourney;
    private final EntityStore entityStore;

    @Given("un erogatore {userRole} del {tenant}")
    public void setProducer(UserRole userRole, Tenant producer) {
        interopJourney.withProducer(producer, userRole);
    }

    @Given("un fruitore {userRole} del {tenant}")
    public void setConsumer(UserRole userRole, Tenant consumer) {
        interopJourney.withConsumer(consumer, userRole);
    }

    @Given("un fruitore {userRole} di/del {tenant} possiede quell'attributo certificato")
    public void setConsumerAssigningLastCertifiedAttribute(UserRole userRole, Tenant consumer) {
        interopJourney
                .withConsumer(consumer, userRole)
                .assignCertifiedAttribute(consumer);
    }
}
