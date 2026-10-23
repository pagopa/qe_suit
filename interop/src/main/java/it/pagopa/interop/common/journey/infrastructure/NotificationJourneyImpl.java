package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.NotificationJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.notification.application.NotificationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationJourneyImpl implements NotificationJourney<NotificationJourneyImpl> {
    private final NotificationGateway gateway;

    @Override
    public NotificationJourneyImpl enableAppNotificationsFor(Tenant tenant) {
        gateway.ensureAllEnabled(tenant);
        return this;
    }

    @Override
    public NotificationJourneyImpl enableAppNotificationsFor(Tenant tenant, User user) {
        gateway.ensureAllEnabled(tenant, user);
        return this;
    }

    @Override
    public NotificationJourneyImpl verifyUnreadNotificationsFor(Tenant consumer, EService eService) {
        gateway.awaitUnreadNotification(TenantRef.of(consumer.getOrganizationId()), eService.getRef());
        return this;
    }
}
