package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.ReadNotificationTimelineGateway;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.NotificationTimeline;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineItemComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Legge la cronologia della notifica dalla pagina "Stato della notifica" del portale della PA: una card per stato,
 * dal più recente, con l'etichetta dello stato come titolo.
 */
@Service
@RequiredArgsConstructor
public class WebReadNotificationTimelineGateway implements ReadNotificationTimelineGateway {

    private final TimelineDetailsPage timelineDetailsPage;

    @Override
    public NotificationTimeline readNotificationTimeline(String iun) {
        timelineDetailsPage.navigateTo(iun);
        timelineDetailsPage.assertLoaded();
        return new NotificationTimeline(iun, timelineDetailsPage.timeline().items().stream()
                .map(TimelineItemComponent::statusLabel)
                .map(label -> NotificationStatus.fromLabel(label).orElseThrow(() ->
                        new IllegalStateException("Stato non riconosciuto nella cronologia della notifica " + iun + ": " + label)))
                .toList());
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}
