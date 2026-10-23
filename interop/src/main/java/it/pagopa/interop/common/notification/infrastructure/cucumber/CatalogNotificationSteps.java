package it.pagopa.interop.common.notification.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.notification.application.CatalogNotificationJourney;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CatalogNotificationSteps {
    private final CatalogNotificationJourney journey;
    @Given("il fruitore {tenant} è abilitato alle notifiche in-app")
    public void enableNotifications(Tenant consumer) {
        journey.enableFor(consumer);
    }
    @Given("una notifica non letta generata per il fruitore {tenant} sull'e-service {eServiceByAlias}")
    public void verifyUnreadNotification(Tenant consumer, EService eService) {
        journey.verifyUnreadFor(consumer, eService);
    }
}
