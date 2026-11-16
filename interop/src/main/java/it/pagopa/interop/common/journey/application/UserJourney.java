package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.kernel.domain.*;
import org.springframework.plugin.core.Plugin;

public interface UserJourney<SELF extends UserJourney<SELF>> extends JourneyModule, Plugin<Channel> {
    SELF withProducer(Tenant tenant);

    SELF withConsumer(Tenant tenant);

    SELF withProducer(Tenant tenant, InteropActor actor);

    SELF withConsumer(Tenant tenant, InteropActor actor);

    SELF withProducer(Tenant tenant, InteropRole role);

    SELF withConsumer(Tenant tenant, InteropRole role);
}
