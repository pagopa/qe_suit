package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;

public interface NotificationJourney<SELF extends NotificationJourney<SELF>> extends JourneyModule {
    SELF enableNotificationsFor(Tenant consumer);

    SELF enableInAppNotificationsFor(Tenant consumer, User user);

    SELF verifyUnreadInAppNotificationsFor(Tenant consumer, User user, EService eService);
}
