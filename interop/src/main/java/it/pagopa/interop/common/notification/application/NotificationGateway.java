package it.pagopa.interop.common.notification.application;

import it.pagopa.interop.common.kernel.domain.*;
import org.springframework.plugin.core.Plugin;

public interface NotificationGateway extends Plugin<Channel> {
    void ensureAllEnabled(Tenant tenant);

    void ensureAllEnabled(Tenant tenant, User user);

    void awaitUnreadNotification(Tenant tenant,  User user, EServiceRef eService);
}
