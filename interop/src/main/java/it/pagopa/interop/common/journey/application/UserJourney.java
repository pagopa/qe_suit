package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;

public interface UserJourney<SELF extends UserJourney<SELF>> extends JourneyModule {
    SELF withProducer(Tenant tenant, User user);

    SELF withConsumer(Tenant tenant, User user);

    SELF withProducer(Tenant tenant, UserRole role);

    SELF withConsumer(Tenant tenant, UserRole role);
}
