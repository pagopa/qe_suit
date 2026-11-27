package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import org.springframework.plugin.core.Plugin;

/** Attende che l'ultima notifica legale creata raggiunga lo stato richiesto. */
public interface WaitForNotificationStatusGateway extends Plugin<Channel> {
    void waitForNotificationStatus(NotificationStatus targetStatus);
}
