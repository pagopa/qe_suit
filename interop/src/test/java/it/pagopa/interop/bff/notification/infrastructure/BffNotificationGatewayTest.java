package it.pagopa.interop.bff.notification.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfigUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.InAppNotificationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.NotificationConfigsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BffNotificationGatewayTest {
    private static final UUID TENANT_ID = Tenant.PAGO_PA.getOrganizationId();
    private static final UUID ESERVICE_ID = UUID.fromString("117cf9f4-a544-4a86-99c0-68ab675f9581");

    @Test
    void notificationSettingsAreIdempotentlyEnabledAndVerified() throws Exception {
        var fixture = fixture();
        AtomicInteger reads = new AtomicInteger();
        when(fixture.getConfig.execute(any())).thenAnswer(call -> response(200,
                "{\"enabled\":" + (reads.getAndIncrement() > 1) + "}"));
        when(fixture.updateConfig.body(any())).thenReturn(fixture.updateConfig);
        when(fixture.updateConfig.execute(any())).thenAnswer(call -> response(204, ""));

        fixture.gateway.enableNotification(Tenant.PAGO_PA);
        fixture.gateway.enableNotification(Tenant.PAGO_PA);

        var seed = org.mockito.ArgumentCaptor.forClass(TenantNotificationConfigUpdateSeed.class);
        verify(fixture.updateConfig).body(seed.capture());
        assertThat(seed.getValue().getEnabled()).isTrue();
        verify(fixture.getConfig, times(4)).execute(any());
        verify(fixture.updateConfig).execute(any());
        assertThat(fixture.gateway.supports(Channel.BFF)).isTrue();
    }

    @Test
    void alreadyEnabledConfigurationDoesNotWrite() {
        var fixture = fixture();
        when(fixture.getConfig.execute(any())).thenAnswer(call -> response(200, "{\"enabled\":true}"));

        fixture.gateway.enableNotification(Tenant.PAGO_PA);

        verify(fixture.getConfig).execute(any());
        verify(fixture.configs, never()).updateTenantNotificationConfig();
    }

    @Test
    void notificationApiMustReturnUnreadEntryForTheSelectedEservice() {
        var fixture = fixture();
        when(fixture.getNotifications.unreadQuery(true)).thenReturn(fixture.getNotifications);
        when(fixture.getNotifications.offsetQuery(0)).thenReturn(fixture.getNotifications);
        when(fixture.getNotifications.limitQuery(50)).thenReturn(fixture.getNotifications);
        when(fixture.getNotifications.execute(any())).thenAnswer(call -> response(200,
                "{\"results\":[{\"tenantId\":\"" + TENANT_ID + "\",\"userId\":\""
                        + User.getTenantAdmin(Tenant.PAGO_PA).getUserId() + "\",\"deepLink\":null},"
                        + "{\"id\":\"4cba6e66-ff8e-416f-914f-a1b5896c749e\","
                        + "\"userId\":\"" + User.getTenantAdmin(Tenant.PAGO_PA).getUserId() + "\","
                        + "\"tenantId\":\"" + TENANT_ID + "\",\"body\":\"descriptor updated\","
                        + "\"deepLink\":\"/catalogo-e-service/" + ESERVICE_ID
                        + "\",\"category\":\"catalogo-e-service\",\"readAt\":null,"
                        + "\"createdAt\":\"2026-10-22T10:00:00Z\"}],"
                        + "\"pagination\":{\"offset\":0,\"limit\":50,\"totalCount\":1}}"));

        fixture.gateway.awaitUnreadNotification(Tenant.PAGO_PA,
                User.getTenantAdmin(Tenant.PAGO_PA), EServiceRef.of(ESERVICE_ID));

        verify(fixture.getNotifications).unreadQuery(true);
        verify(fixture.getNotifications).offsetQuery(0);
        verify(fixture.getNotifications).limitQuery(50);
    }

    @Test
    void authorizationFailureIsNotTreatedAsAnUnconfiguredTenant() {
        var fixture = fixture();
        when(fixture.getConfig.execute(any())).thenAnswer(call -> response(403, "{}"));

        assertThatThrownBy(() -> fixture.gateway.enableNotification(Tenant.PAGO_PA))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(org.awaitility.core.ConditionTimeoutException.class);
        verify(fixture.configs, never()).updateTenantNotificationConfig();
    }

    @ParameterizedTest
    @CsvSource({"ADMIN, false", "ADMIN, true", "REVIEWER, false", "REVIEWER, true"})
    void userSettingsEnableRoleAllowedCategoriesAndPreserveEmailPreferences(UserRole role, boolean preference)
            throws Exception {
        var fixture = fixture();
        var getUser = mock(NotificationConfigsApi.GetUserNotificationConfigOper.class);
        var updateUser = mock(NotificationConfigsApi.UpdateUserNotificationConfigOper.class);
        when(fixture.configs.getUserNotificationConfig()).thenReturn(getUser);
        when(fixture.configs.updateUserNotificationConfig()).thenReturn(updateUser);
        when(updateUser.body(any())).thenReturn(updateUser);
        when(updateUser.execute(any())).thenReturn(response(204, ""));
        var email = new NotificationConfig().eserviceStateChangedToConsumer(false);
        var before = new UserNotificationConfig()
                .inAppNotificationPreference(preference).inAppConfig(new NotificationConfig())
                .emailNotificationPreference(true).emailDigestPreference(false).emailConfig(email);
        var enabled = role == UserRole.REVIEWER
                ? new BffNotificationRequestFactory().allEnabledInAppConfig() : adminConfig();
        var after = new UserNotificationConfig()
                .inAppNotificationPreference(true).inAppConfig(enabled)
                .emailNotificationPreference(true).emailDigestPreference(false).emailConfig(email);
        var json = new ObjectMapper();
        when(getUser.execute(any())).thenReturn(
                response(200, json.writeValueAsString(before)),
                response(200, json.writeValueAsString(before)),
                response(200, json.writeValueAsString(after)),
                response(200, json.writeValueAsString(after)));
        var targetTenant = Tenant.PAGO_PA;
        var targetUser = User.getTenantUser(targetTenant, role);

        fixture.gateway.enableAllInAppNotification(targetTenant, targetUser);
        fixture.gateway.enableAllInAppNotification(targetTenant, targetUser);

        var payload = org.mockito.ArgumentCaptor.forClass(UserNotificationConfigUpdateSeed.class);
        verify(updateUser).body(payload.capture());
        assertThat(payload.getValue().getInAppNotificationPreference()).isTrue();
        assertThat(payload.getValue().getInAppConfig()).isEqualTo(enabled);
        assertThat(payload.getValue().getEmailConfig()).isEqualTo(email);
        assertThat(payload.getValue().getEmailNotificationPreference()).isTrue();
        assertThat(payload.getValue().getEmailDigestPreference()).isFalse();
        verify(getUser, times(4)).execute(any());
        verify(updateUser).execute(any());
        verify(fixture.configs, never()).updateTenantNotificationConfig();
    }

    @Test
    void userReadFailurePropagatesWithoutWriting() {
        var fixture = fixture();
        var getUser = mock(NotificationConfigsApi.GetUserNotificationConfigOper.class);
        when(fixture.configs.getUserNotificationConfig()).thenReturn(getUser);
        var failure = new IllegalStateException("user settings unavailable");
        when(getUser.execute(any())).thenThrow(failure);

        assertThatThrownBy(() -> fixture.gateway.enableAllInAppNotification(
                Tenant.COMUNE_DI_MILANO, User.getTenantAdmin(Tenant.COMUNE_DI_MILANO))).isSameAs(failure);

        verify(fixture.configs, never()).updateUserNotificationConfig();
    }

    @Test
    void userUpdateFailurePropagates() throws Exception {
        var fixture = fixture();
        var getUser = mock(NotificationConfigsApi.GetUserNotificationConfigOper.class);
        var updateUser = mock(NotificationConfigsApi.UpdateUserNotificationConfigOper.class);
        when(fixture.configs.getUserNotificationConfig()).thenReturn(getUser);
        when(fixture.configs.updateUserNotificationConfig()).thenReturn(updateUser);
        when(updateUser.body(any())).thenReturn(updateUser);
        var config = new UserNotificationConfig().inAppNotificationPreference(false);
        when(getUser.execute(any())).thenReturn(response(200, new ObjectMapper().writeValueAsString(config)));
        var failure = new IllegalStateException("update failed");
        when(updateUser.execute(any())).thenThrow(failure);

        assertThatThrownBy(() -> fixture.gateway.enableAllInAppNotification(
                Tenant.COMUNE_DI_MILANO, User.getTenantAdmin(Tenant.COMUNE_DI_MILANO))).isSameAs(failure);

        verify(updateUser).execute(any());
    }

    @Test
    void userUpdateWaitsUntilReadBackContainsAllEnabledCategories() throws Exception {
        var fixture = fixture();
        var getUser = mock(NotificationConfigsApi.GetUserNotificationConfigOper.class);
        var updateUser = mock(NotificationConfigsApi.UpdateUserNotificationConfigOper.class);
        when(fixture.configs.getUserNotificationConfig()).thenReturn(getUser);
        when(fixture.configs.updateUserNotificationConfig()).thenReturn(updateUser);
        when(updateUser.body(any())).thenReturn(updateUser);
        when(updateUser.execute(any())).thenReturn(response(204, ""));
        var config = new UserNotificationConfig().inAppNotificationPreference(true)
                .inAppConfig(adminConfig()
                        .eserviceStateChangedToConsumer(false));
        var enabled = new UserNotificationConfig().inAppNotificationPreference(true)
                .inAppConfig(adminConfig());
        var json = new ObjectMapper();
        when(getUser.execute(any())).thenReturn(
                response(200, json.writeValueAsString(config)),
                response(200, json.writeValueAsString(config)),
                response(200, json.writeValueAsString(config)),
                response(200, json.writeValueAsString(enabled)));

        fixture.gateway.enableAllInAppNotification(
                Tenant.COMUNE_DI_MILANO, User.getTenantAdmin(Tenant.COMUNE_DI_MILANO));

        verify(updateUser).execute(any());
        verify(getUser, times(4)).execute(any());
    }

    @Test
    void alreadyEnabledTenantRequiresOnlyOneReadOnTheAuthenticatedClient() {
        var fixture = fixture();
        when(fixture.getConfig.execute(any())).thenReturn(response(200, "{\"enabled\":true}"));

        fixture.gateway.enableNotification(Tenant.COMUNE_DI_MILANO);

        verify(fixture.getConfig).execute(any());
        verify(fixture.configs, never()).updateTenantNotificationConfig();
    }

    @Test
    void tenantUpdateFailurePropagatesWithoutReadingAgain() {
        var fixture = fixture();
        when(fixture.getConfig.execute(any())).thenReturn(response(200, "{\"enabled\":false}"));
        when(fixture.updateConfig.body(any())).thenReturn(fixture.updateConfig);
        var failure = new IllegalStateException("tenant update failed");
        when(fixture.updateConfig.execute(any())).thenThrow(failure);

        assertThatThrownBy(() -> fixture.gateway.enableNotification(Tenant.COMUNE_DI_MILANO))
                .isSameAs(failure);

        verify(fixture.getConfig).execute(any());
        verify(fixture.updateConfig).execute(any());
    }

    @Test
    void missingTenantEnabledFlagIsTreatedAsDisabled() {
        var fixture = fixture();
        when(fixture.getConfig.execute(any())).thenReturn(
                response(200, "{}"), response(200, "{\"enabled\":true}"));
        when(fixture.updateConfig.body(any())).thenReturn(fixture.updateConfig);
        when(fixture.updateConfig.execute(any())).thenReturn(response(204, ""));

        fixture.gateway.enableNotification(Tenant.PAGO_PA);

        verify(fixture.updateConfig).execute(any());
    }

    @Test
    void eServiceIdCorrelationRequiresACompleteUuidToken() {
        assertThat(BffNotificationGateway.containsEServiceIdToken(
                "/catalogo-e-service/" + ESERVICE_ID + "?tab=details", ESERVICE_ID)).isTrue();
        assertThat(BffNotificationGateway.containsEServiceIdToken(
                "/catalogo-e-service/" + ESERVICE_ID + "a", ESERVICE_ID)).isFalse();
        assertThat(BffNotificationGateway.containsEServiceIdToken(
                "/catalogo-e-service/x" + ESERVICE_ID, ESERVICE_ID)).isFalse();
        assertThat(BffNotificationGateway.containsEServiceIdToken(null, ESERVICE_ID)).isFalse();
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
        var rest = new BffNotificationRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)), api);
        return new Fixture(configs, getConfig, updateConfig, getNotifications,
                new BffNotificationGateway(rest, new BffNotificationRequestFactory()));
    }

    private NotificationConfig adminConfig() {
        return new BffNotificationRequestFactory().allEnabledInAppConfig()
                .purposeRiskAnalysisAssignmentStatusToReviewer(false)
                .purposePublishedWithRiskAnalysisToReviewer(false);
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
        private final BffNotificationGateway gateway;

        private Fixture(
                NotificationConfigsApi configs,
                NotificationConfigsApi.GetTenantNotificationConfigOper getConfig,
                NotificationConfigsApi.UpdateTenantNotificationConfigOper updateConfig,
                InAppNotificationsApi.GetNotificationsOper getNotifications,
                BffNotificationGateway gateway
        ) {
            this.configs = configs;
            this.getConfig = getConfig;
            this.updateConfig = updateConfig;
            this.getNotifications = getNotifications;
            this.gateway = gateway;
        }
    }
}
