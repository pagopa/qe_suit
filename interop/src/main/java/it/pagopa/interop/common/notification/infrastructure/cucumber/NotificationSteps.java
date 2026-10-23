package it.pagopa.interop.common.notification.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationSteps {
    private final InteropJourney journey;

    @Given("l'erogatore {tenant} è abilitato alle notifiche in-app")
    @Given("il fruitore {tenant} è abilitato alle notifiche in-app")
    public void enableNotifications(Tenant tenant) {
        journey
                .withConsumer(tenant, UserRole.ADMIN)
                .enableNotificationsFor(tenant);
    }

    @Given("l'utente {userRole} del fruitore/erogatore {tenant} è abilitato alle notifiche in-app")
    public void enableInAppNotifications(UserRole userRole, Tenant tenant) {
        journey
                .withConsumer(tenant, userRole)
                .enableInAppNotificationsFor(tenant, User.getTenantUser(tenant, userRole));
    }

    @Given("una notifica non letta generata per l'utente {userRole} del fruitore/erogatore {tenant} sul{eServiceByAlias}")
    public void verifyUnreadInAppNotification(UserRole userRole, Tenant tenant, EService eService) {
        journey
                .withConsumer(tenant, userRole)
                .verifyUnreadInAppNotificationsFor(tenant, User.getTenantUser(tenant, userRole), eService);
    }
}
