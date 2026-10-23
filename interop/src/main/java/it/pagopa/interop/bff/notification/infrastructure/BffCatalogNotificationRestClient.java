package it.pagopa.interop.bff.notification.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.InAppNotificationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.NotificationConfigsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notifications;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import org.springframework.stereotype.Component;

@Component
public class BffCatalogNotificationRestClient extends RestClient {
    private final NotificationConfigsApi configs;
    private final InAppNotificationsApi notifications;
    public BffCatalogNotificationRestClient(TestChainFactory chains, ApiClient client) {
        super(chains);
        configs = client.notificationConfigs();
        notifications = client.inAppNotifications();
    }
    public TestChain<TenantNotificationConfig> getTenantConfig() {
        return execute(() -> configs.getTenantNotificationConfig().execute(java.util.function.Function.identity()),
                TenantNotificationConfig.class);
    }
    public TestChain<Void> enableTenantConfig() {
        return execute(() -> configs.updateTenantNotificationConfig()
                .body(new TenantNotificationConfigUpdateSeed().enabled(true))
                .execute(java.util.function.Function.identity()), Void.class);
    }
    public TestChain<Notifications> getUnreadNotifications(int offset, int limit) {
        return execute(() -> notifications.getNotifications().unreadQuery(true)
                .offsetQuery(offset).limitQuery(limit).execute(java.util.function.Function.identity()),
                Notifications.class);
    }
}
