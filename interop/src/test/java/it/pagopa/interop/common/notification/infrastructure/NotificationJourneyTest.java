package it.pagopa.interop.common.notification.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.infrastructure.NotificationJourneyImpl;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.notification.application.NotificationGateway;
import it.pagopa.interop.common.kernel.domain.TenantRef;
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
            assertThatThrownBy(() -> journey.enableAppNotificationsFor(Tenant.PAGO_PA)).isSameAs(failure);
        } else {
            journey.enableAppNotificationsFor(Tenant.PAGO_PA);
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
            assertThatThrownBy(() -> journey.enableAppNotificationsFor(Tenant.PAGO_PA, user)).isSameAs(failure);
        } else {
            assertThat(journey.enableAppNotificationsFor(Tenant.PAGO_PA, user)).isSameAs(journey);
        }
        verify(gateway).enableAllInAppNotification(Tenant.PAGO_PA, user);
        verifyNoMoreInteractions(gateway);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unreadNotificationVerificationUsesExplicitConsumerAndPropagatesFailure(boolean fail) {
        var gateway = mock(NotificationGateway.class);
        var eService = EService.builder().id(java.util.UUID.randomUUID()).build();
        var failure = new IllegalStateException("notification missing");
        if (fail) doThrow(failure).when(gateway).awaitUnreadNotification(
                TenantRef.of(Tenant.PAGO_PA.getOrganizationId()), eService.getRef());

        var journey = new NotificationJourneyImpl(gateway);
        if (fail) {
            assertThatThrownBy(() -> journey.verifyUnreadInAppNotificationsFor(Tenant.PAGO_PA, eService)).isSameAs(failure);
        } else {
            journey.verifyUnreadInAppNotificationsFor(Tenant.PAGO_PA, eService);
        }
        verify(gateway).awaitUnreadNotification(TenantRef.of(Tenant.PAGO_PA.getOrganizationId()), eService.getRef());
        verifyNoMoreInteractions(gateway);
    }
}
