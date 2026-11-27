package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.ReadNotificationTimelineGateway;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.NotificationTimeline;
import it.pagopa.send.common.legal_notification.domain.NotificationTimeline.StatusChange;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineItemComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Legge la cronologia della notifica dalla pagina "Stato della notifica" del portale della PA: una card per stato,
 * dal più recente, con l'etichetta dello stato come titolo e la data da cui è attivo.
 */
@Service
@RequiredArgsConstructor
public class WebReadNotificationTimelineGateway implements ReadNotificationTimelineGateway {

    private final TimelineDetailsPage timelineDetailsPage;
    private final WebPortalDateMapper dateMapper;

    @Override
    public NotificationTimeline readNotificationTimeline(String iun) {
        timelineDetailsPage.navigateTo(iun);
        timelineDetailsPage.assertLoaded();
        return new NotificationTimeline(iun, timelineDetailsPage.timeline().items().stream()
                .map(item -> toStatusChange(iun, item))
                .toList());
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    private StatusChange toStatusChange(String iun, TimelineItemComponent item) {
        String label = item.statusLabel();
        NotificationStatus status = NotificationStatus.fromLabel(label).orElseThrow(() ->
                new IllegalStateException("Stato non riconosciuto nella cronologia della notifica " + iun + ": " + label));
        return new StatusChange(status, item.statusDate().get(FindPolicy.PRESENT)
                .map(date -> dateMapper.fromTimelineDate(date.getText()))
                .orElse(null));
    }
}
