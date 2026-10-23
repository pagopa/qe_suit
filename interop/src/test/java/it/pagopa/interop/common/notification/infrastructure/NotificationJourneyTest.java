package it.pagopa.interop.common.notification.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.infrastructure.NotificationJourneyImpl;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.notification.application.NotificationGateway;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationJourneyTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void delegatesTenantNotificationEnablementAndPropagatesFailures(boolean fail) {
        var gateway = mock(NotificationGateway.class);
        var failure = new IllegalStateException("config failed");
        if (fail) doThrow(failure).when(gateway).enableNotification(Tenant.PAGO_PA);

        var journey = new NotificationJourneyImpl(gateway);
        if (fail) {
            assertThatThrownBy(() -> journey.enableNotificationsFor(Tenant.PAGO_PA)).isSameAs(failure);
        } else {
            assertThat(journey.enableNotificationsFor(Tenant.PAGO_PA)).isSameAs(journey);
        }
        verify(gateway).enableNotification(Tenant.PAGO_PA);
        verifyNoMoreInteractions(gateway);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void delegatesUserNotificationEnablementAndPropagatesFailures(boolean fail) {
        var gateway = mock(NotificationGateway.class);
        var user = User.getTenantAdmin(Tenant.PAGO_PA);
        var failure = new IllegalStateException("user config failed");
        if (fail) doThrow(failure).when(gateway).enableAllInAppNotification(Tenant.PAGO_PA, user);
        var journey = new NotificationJourneyImpl(gateway);

        if (fail) {
            assertThatThrownBy(() -> journey.enableInAppNotificationsFor(Tenant.PAGO_PA, user)).isSameAs(failure);
        } else {
            assertThat(journey.enableInAppNotificationsFor(Tenant.PAGO_PA, user)).isSameAs(journey);
        }
        verify(gateway).enableAllInAppNotification(Tenant.PAGO_PA, user);
        verifyNoMoreInteractions(gateway);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unreadNotificationVerificationUsesExplicitConsumerAndPropagatesFailure(boolean fail) {
        var gateway = mock(NotificationGateway.class);
        var eService = EService.builder().id(java.util.UUID.randomUUID()).build();
        var user = User.getTenantAdmin(Tenant.PAGO_PA);
        var failure = new IllegalStateException("notification missing");
        if (fail) doThrow(failure).when(gateway).awaitUnreadNotification(
                Tenant.PAGO_PA, user, eService.getRef());

        var journey = new NotificationJourneyImpl(gateway);
        if (fail) {
            assertThatThrownBy(() -> journey.verifyUnreadInAppNotificationsFor(Tenant.PAGO_PA, user, eService))
                    .isSameAs(failure);
        } else {
            assertThat(journey.verifyUnreadInAppNotificationsFor(Tenant.PAGO_PA, user, eService))
                    .isSameAs(journey);
        }
        verify(gateway).awaitUnreadNotification(Tenant.PAGO_PA, user, eService.getRef());
        verifyNoMoreInteractions(gateway);
    }
}
