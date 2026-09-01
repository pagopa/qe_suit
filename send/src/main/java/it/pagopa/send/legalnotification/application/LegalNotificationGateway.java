package it.pagopa.send.legalnotification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.notification.domain.LegalNotificationDomain;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import org.springframework.plugin.core.Plugin;

import java.util.Map;

public interface LegalNotificationGateway extends Plugin<Channel> {
    void sendNotification(BffNewNotificationRequest request, BffNotificationStatus targetStatus);
    void deleteNotification(String iun);
    LegalNotificationDomain readNotification(String iun);
    LegalNotificationDomain searchNotification(Map<String, String> overrides);

}
