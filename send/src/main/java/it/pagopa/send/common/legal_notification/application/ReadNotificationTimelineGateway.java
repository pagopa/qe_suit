package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.NotificationTimeline;
import org.springframework.plugin.core.Plugin;

/** Legge la cronologia degli stati della notifica legale indicata, come la vede la PA mittente. */
public interface ReadNotificationTimelineGateway extends Plugin<Channel> {
    NotificationTimeline readNotificationTimeline(String iun);
}
