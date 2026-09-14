package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import org.springframework.plugin.core.Plugin;

import java.util.Map;

/**
 * Ogni implementazione conosce come costruire ed inviare una notifica per il proprio canale.
 * {@code prepareNotification}/{@code addRecipient}/{@code sendNotification} popolano
 * progressivamente, ciascuna a modo suo, la request nativa del canale (es. il BFF costruisce già
 * la propria {@code LegalNotificationCreationRequest} interna ad ogni chiamata; il Web per ora non
 * fa nulla, in attesa dell'implementazione).
 */
public interface LegalNotificationGateway extends Plugin<Channel> {
    void prepareNotification(Map<String, String> data);
    void addRecipient(RecipientSpec recipient);
    void sendNotification(Tenant sender, NotificationStatus targetStatus);
    void waitForNotificationStatus(NotificationStatus targetStatus);
    void deleteNotification(String iun);
    LegalNotificationDomain readNotification(String iun);
    LegalNotificationDomain searchNotification(Map<String, String> overrides);
}
