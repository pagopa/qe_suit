package it.pagopa.interop.common.notification.infrastructure;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.notification.application.CatalogNotificationGateway;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogNotificationJourneyTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void enablesAsConsumerAdminAndRestoresUserAndChannel(boolean fail) {
        var gateway = mock(CatalogNotificationGateway.class);
        var session = mock(CurrentUserSession.class);
        @SuppressWarnings("unchecked")
        CurrentChannel<Channel> channel = mock(CurrentChannel.class);
        var originalTenant = Tenant.COMUNE_DI_MILANO;
        var originalUser = User.getTenantAdmin(originalTenant);
        when(session.getTenant()).thenReturn(originalTenant);
        when(session.getUser()).thenReturn(originalUser);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);
        var failure = new IllegalStateException("config failed");
        if (fail) doThrow(failure).when(gateway).ensureEnabled(TenantRef.of(Tenant.PAGO_PA.getOrganizationId()));

        var journey = new CatalogNotificationJourneyImpl(gateway, session, channel);
        if (fail) {
            assertThatThrownBy(() -> journey.enableFor(Tenant.PAGO_PA)).isSameAs(failure);
        } else {
            journey.enableFor(Tenant.PAGO_PA);
        }
        var order = inOrder(channel, session, gateway);
        order.verify(channel).setCurrentChannel(Channel.BFF);
        order.verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
        order.verify(gateway).ensureEnabled(TenantRef.of(Tenant.PAGO_PA.getOrganizationId()));
        order.verify(session).set(originalUser, originalTenant);
        order.verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unreadNotificationVerificationUsesExplicitConsumerAndRestoresOnFailure(boolean fail) {
        var gateway = mock(CatalogNotificationGateway.class);
        var session = mock(CurrentUserSession.class);
        @SuppressWarnings("unchecked")
        CurrentChannel<Channel> channel = mock(CurrentChannel.class);
        var originalTenant = Tenant.COMUNE_DI_MILANO;
        var originalUser = User.getTenantAdmin(originalTenant);
        var eService = EService.builder().id(java.util.UUID.randomUUID()).build();
        when(session.getTenant()).thenReturn(originalTenant);
        when(session.getUser()).thenReturn(originalUser);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);
        var failure = new IllegalStateException("notification missing");
        if (fail) doThrow(failure).when(gateway).awaitUnreadNotification(
                TenantRef.of(Tenant.PAGO_PA.getOrganizationId()), eService.getRef());

        var journey = new CatalogNotificationJourneyImpl(gateway, session, channel);
        if (fail) {
            assertThatThrownBy(() -> journey.verifyUnreadFor(Tenant.PAGO_PA, eService)).isSameAs(failure);
        } else {
            journey.verifyUnreadFor(Tenant.PAGO_PA, eService);
        }
        verify(session).set(originalUser, originalTenant);
        verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
        verify(gateway).awaitUnreadNotification(TenantRef.of(Tenant.PAGO_PA.getOrganizationId()), eService.getRef());
    }
}
