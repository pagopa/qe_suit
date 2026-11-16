package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.kernel.domain.*;

public interface UserJourney<SELF extends UserJourney<SELF>> extends JourneyModule {
    SELF withProducer(Tenant tenant);

    SELF withConsumer(Tenant tenant);

    SELF withProducer(Tenant tenant, InteropActor actor);

    SELF withConsumer(Tenant tenant, InteropActor actor);

    SELF withProducer(Tenant tenant, InteropRole role);

    SELF withConsumer(Tenant tenant, InteropRole role);
}
