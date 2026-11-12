package it.pagopa.interop.bff.notification.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.InAppNotificationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.NotificationConfigsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.springframework.stereotype.Component;

@Component
public class BffNotificationRestClient extends RestClient {
    private final NotificationConfigsApi configs;
    private final InAppNotificationsApi notifications;

    public BffNotificationRestClient(TestChainFactory chains, ApiClient client) {
        super(chains);
        configs = client.notificationConfigs();
        notifications = client.inAppNotifications();
    }

    public TestChain<TenantNotificationConfig> getTenantNotificationConfig() {
        return execute(() -> configs.getTenantNotificationConfig().execute(java.util.function.Function.identity()),
                TenantNotificationConfig.class);
    }

    public TestChain<UserNotificationConfig> getUserNotificationConfig() {
        return execute(() -> configs.getUserNotificationConfig().execute(java.util.function.Function.identity()),
                UserNotificationConfig.class);
    }

    public TestChain<Void> updateTenantNotificationConfig(TenantNotificationConfigUpdateSeed seed) {
        return execute(() -> configs.updateTenantNotificationConfig()
                .body(seed)
                .execute(java.util.function.Function.identity()), Void.class);
    }

    public TestChain<Void> updateUserNotificationConfig(UserNotificationConfigUpdateSeed seed) {
        return execute(() -> configs.updateUserNotificationConfig()
                .body(seed)
                .execute(java.util.function.Function.identity()), Void.class);
    }

    public TestChain<Notifications> getNotifications(Boolean unread, int offset, int limit) {
        return execute(() -> notifications.getNotifications().unreadQuery(unread).offsetQuery(offset).limitQuery(limit).execute(java.util.function.Function.identity()),
                Notifications.class);
    }
}
