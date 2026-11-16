package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.NotificationJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.notification.application.NotificationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationJourneyImpl implements NotificationJourney<NotificationJourneyImpl> {
    private final NotificationGateway gateway;

    @Override
    public NotificationJourneyImpl enableNotificationsFor(Tenant tenant) {
        gateway.enableNotification(tenant);
        return this;
    }

    @Override
    public NotificationJourneyImpl enableInAppNotificationsFor(Tenant tenant, User user) {
        gateway.enableAllInAppNotification(tenant, user);
        return this;
    }

    @Override
    public NotificationJourneyImpl verifyUnreadInAppNotificationsFor(Tenant tenant, User user, EService eService) {
        gateway.awaitUnreadNotification(tenant, user, eService.getRef());
        return this;
    }
}
