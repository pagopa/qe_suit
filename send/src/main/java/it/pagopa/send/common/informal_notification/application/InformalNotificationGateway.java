package it.pagopa.send.common.informal_notification.application;

import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.generated.openapi.clients.informal.model.FullSentInformalNotificationV1;
import org.springframework.plugin.core.Plugin;

import java.util.Map;

public interface InformalNotificationGateway extends Plugin<Channel> {
    void prepareNotification(Map<String, String> data);
    void addRecipient(User user, InformalRecipientSpec recipient);
    InformalNotificationDomain sendNotification(User sender);
    FullSentInformalNotificationV1 getInformalNotification(String iun);
    FullSentInformalNotificationV1 getInformalNotification(String iun,int interval);
    FullSentInformalNotificationV1 getInformalNotification();
}
