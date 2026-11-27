package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import org.springframework.plugin.core.Plugin;

/** Legge la notifica legale indicata e la registra come ultima notifica dello scenario. */
public interface ReadNotificationGateway extends Plugin<Channel> {
    LegalNotificationDomain readNotification(String iun);
}
