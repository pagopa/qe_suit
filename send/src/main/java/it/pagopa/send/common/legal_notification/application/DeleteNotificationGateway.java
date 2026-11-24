package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

/** Annulla la notifica legale indicata e attende che risulti annullata. */
public interface DeleteNotificationGateway extends Plugin<Channel> {
    void deleteNotification(String iun);
}
