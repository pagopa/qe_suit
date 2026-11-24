package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.user.domain.User;
import org.springframework.plugin.core.Plugin;

/** Invia la notifica legale preparata, per il mittente indicato, e ne attende lo stato richiesto. */
public interface SendNotificationGateway extends Plugin<Channel> {
    void sendNotification(User sender, NotificationStatus targetStatus);
}
