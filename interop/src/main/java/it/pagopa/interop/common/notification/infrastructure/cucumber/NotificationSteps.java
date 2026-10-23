package it.pagopa.interop.common.notification.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationSteps {
    private final InteropJourney journey;

    @Given("il fruitore {tenant} è abilitato alle notifiche in-app")
    public void enableNotifications(Tenant consumer) {
        journey
                .withConsumer(consumer, UserRole.ADMIN)
                .enableAppNotificationsFor(consumer);
    }

    @Given("una notifica non letta generata per il fruitore {tenant} sul{eServiceByAlias}")
    public void verifyUnreadNotification(Tenant consumer, EService eService) {
        journey
                .withConsumer(consumer, UserRole.ADMIN)
                .verifyUnreadNotificationsFor(consumer, eService);
    }
}
