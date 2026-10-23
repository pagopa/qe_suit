package it.pagopa.interop.bff.notification.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.InAppNotificationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.NotificationConfigsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BffCatalogNotificationGatewayTest {
    private static final UUID TENANT_ID = Tenant.PAGO_PA.getOrganizationId();
    private static final UUID ESERVICE_ID = UUID.fromString("117cf9f4-a544-4a86-99c0-68ab675f9581");

    @Test
    void notificationSettingsAreIdempotentlyEnabledAndVerified() throws Exception {
        var fixture = fixture();
        AtomicInteger reads = new AtomicInteger();
        when(fixture.getConfig.execute(any())).thenAnswer(call -> response(200,
                "{\"enabled\":" + (reads.getAndIncrement() > 0) + "}"));
        when(fixture.updateConfig.body(any())).thenReturn(fixture.updateConfig);
        when(fixture.updateConfig.execute(any())).thenAnswer(call -> response(204, ""));

        fixture.gateway.ensureEnabled(TenantRef.of(TENANT_ID));
        fixture.gateway.ensureEnabled(TenantRef.of(TENANT_ID));

        var seed = org.mockito.ArgumentCaptor.forClass(TenantNotificationConfigUpdateSeed.class);
        verify(fixture.updateConfig).body(seed.capture());
        assertThat(seed.getValue().getEnabled()).isTrue();
        verify(fixture.getConfig, times(3)).execute(any());
        verify(fixture.updateConfig).execute(any());
        assertThat(fixture.gateway.supports(Channel.BFF)).isTrue();
    }

    @Test
    void alreadyEnabledConfigurationDoesNotWrite() {
        var fixture = fixture();
        when(fixture.getConfig.execute(any())).thenAnswer(call -> response(200, "{\"enabled\":true}"));

        fixture.gateway.ensureEnabled(TenantRef.of(TENANT_ID));

        verify(fixture.getConfig).execute(any());
        verify(fixture.configs, never()).updateTenantNotificationConfig();
    }

    @Test
    void notificationApiMustReturnUnreadEntryForTheSelectedEservice() {
        var fixture = fixture();
        when(fixture.getNotifications.unreadQuery(true)).thenReturn(fixture.getNotifications);
        when(fixture.getNotifications.offsetQuery(0)).thenReturn(fixture.getNotifications);
        when(fixture.getNotifications.limitQuery(100)).thenReturn(fixture.getNotifications);
        when(fixture.getNotifications.execute(any())).thenAnswer(call -> response(200,
                "{\"results\":[{\"id\":\"4cba6e66-ff8e-416f-914f-a1b5896c749e\","
                        + "\"userId\":\"4cba6e66-ff8e-416f-914f-a1b5896c749e\","
                        + "\"tenantId\":\"" + TENANT_ID + "\",\"body\":\"descriptor updated\","
                        + "\"deepLink\":\"/catalogo-e-service/" + ESERVICE_ID
                        + "\",\"category\":\"catalogo-e-service\",\"readAt\":null,"
                        + "\"createdAt\":\"2026-10-22T10:00:00Z\"}],"
                        + "\"pagination\":{\"offset\":0,\"limit\":100,\"totalCount\":1}}"));

        fixture.gateway.awaitUnreadNotification(TenantRef.of(TENANT_ID), EServiceRef.of(ESERVICE_ID));

        verify(fixture.getNotifications).unreadQuery(true);
        verify(fixture.getNotifications).offsetQuery(0);
        verify(fixture.getNotifications).limitQuery(100);
    }

    @Test
    void authorizationFailureIsNotTreatedAsAnUnconfiguredTenant() {
        var fixture = fixture();
        when(fixture.getConfig.execute(any())).thenAnswer(call -> response(403, "{}"));

        assertThatThrownBy(() -> fixture.gateway.ensureEnabled(TenantRef.of(TENANT_ID)))
                .isInstanceOf(AssertionError.class).hasMessageContaining("403");
        verify(fixture.configs, never()).updateTenantNotificationConfig();
    }

    @Test
    void eServiceIdCorrelationRequiresACompleteUuidToken() {
        assertThat(BffCatalogNotificationGateway.containsEServiceIdToken(
                "/catalogo-e-service/" + ESERVICE_ID + "?tab=details", ESERVICE_ID)).isTrue();
        assertThat(BffCatalogNotificationGateway.containsEServiceIdToken(
                "/catalogo-e-service/" + ESERVICE_ID + "a", ESERVICE_ID)).isFalse();
        assertThat(BffCatalogNotificationGateway.containsEServiceIdToken(
                "/catalogo-e-service/x" + ESERVICE_ID, ESERVICE_ID)).isFalse();
        assertThat(BffCatalogNotificationGateway.containsEServiceIdToken(null, ESERVICE_ID)).isFalse();
    }

    private Fixture fixture() {
        var api = mock(ApiClient.class);
        var configs = mock(NotificationConfigsApi.class);
        var getConfig = mock(NotificationConfigsApi.GetTenantNotificationConfigOper.class);
        var updateConfig = mock(NotificationConfigsApi.UpdateTenantNotificationConfigOper.class);
        var notifications = mock(InAppNotificationsApi.class);
        var getNotifications = mock(InAppNotificationsApi.GetNotificationsOper.class);
        when(api.notificationConfigs()).thenReturn(configs);
        when(api.inAppNotifications()).thenReturn(notifications);
        when(configs.getTenantNotificationConfig()).thenReturn(getConfig);
        when(configs.updateTenantNotificationConfig()).thenReturn(updateConfig);
        when(notifications.getNotifications()).thenReturn(getNotifications);
        var rest = new BffCatalogNotificationRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)), api);
        var session = mock(CurrentUserSession.class);
        when(session.getTenant()).thenReturn(Tenant.PAGO_PA);
        return new Fixture(configs, getConfig, updateConfig, getNotifications,
                new BffCatalogNotificationGateway(rest, session));
    }

    private Response response(int status, String body) {
        var builder = new ResponseBuilder().setStatusCode(status).setContentType(ContentType.JSON);
        if (!body.isEmpty()) builder.setBody(body);
        return builder.build();
    }

    private static final class Fixture {
        private final NotificationConfigsApi configs;
        private final NotificationConfigsApi.GetTenantNotificationConfigOper getConfig;
        private final NotificationConfigsApi.UpdateTenantNotificationConfigOper updateConfig;
        private final InAppNotificationsApi.GetNotificationsOper getNotifications;
        private final BffCatalogNotificationGateway gateway;

        private Fixture(
                NotificationConfigsApi configs,
                NotificationConfigsApi.GetTenantNotificationConfigOper getConfig,
                NotificationConfigsApi.UpdateTenantNotificationConfigOper updateConfig,
                InAppNotificationsApi.GetNotificationsOper getNotifications,
                BffCatalogNotificationGateway gateway
        ) {
            this.configs = configs;
            this.getConfig = getConfig;
            this.updateConfig = updateConfig;
            this.getNotifications = getNotifications;
            this.gateway = gateway;
        }
    }
}
