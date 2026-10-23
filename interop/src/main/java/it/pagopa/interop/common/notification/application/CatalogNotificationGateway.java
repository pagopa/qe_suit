package it.pagopa.interop.common.notification.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import org.springframework.plugin.core.Plugin;

public interface CatalogNotificationGateway extends Plugin<Channel> {
    void ensureEnabled(TenantRef tenant);
    void awaitUnreadNotification(TenantRef tenant, EServiceRef eService);
}
