package it.pagopa.send.common.informal_notification.application;

import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.Tenant;
import org.springframework.plugin.core.Plugin;

import java.util.Map;

public interface InformalNotificationGateway extends Plugin<Channel> {
    void prepareNotification(Map<String, String> data);
    void addRecipient(Tenant sender, InformalRecipientSpec recipient);
    InformalNotificationDomain sendNotification(Tenant sender);
}
