package it.pagopa.interop.common.notification.infrastructure.cucumber;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class NotificationStepsTest {
    @Test
    void enablesNotificationsUsingTheExplicitConsumerAdmin() {
        var journey = mock(InteropJourney.class, RETURNS_SELF);

        new NotificationSteps(journey).enableNotifications(Tenant.PAGO_PA);

        var order = inOrder(journey);
        order.verify(journey).withConsumer(Tenant.PAGO_PA, UserRole.ADMIN);
        order.verify(journey).enableAppNotificationsFor(Tenant.PAGO_PA);
        order.verifyNoMoreInteractions();
    }

    @Test
    void verifiesUnreadNotificationUsingTheExplicitConsumerAndEservice() {
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var service = EService.builder().id(UUID.randomUUID()).build();

        new NotificationSteps(journey).verifyUnreadInAppNotification(UserRole.ADMIN, Tenant.PAGO_PA, service);

        var order = inOrder(journey);
        order.verify(journey).withConsumer(Tenant.PAGO_PA, UserRole.ADMIN);
        order.verify(journey).verifyUnreadInAppNotificationsFor(Tenant.PAGO_PA, service);
        order.verifyNoMoreInteractions();
    }
}
