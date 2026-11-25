package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.ReadNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.section.MittenteNotificationSummarySection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Legge il dettaglio della notifica nel portale della PA (IUN, numero di protocollo, mittente, stato) e lo registra
 * come ultima notifica, come il canale BFF.
 */
@Service
@RequiredArgsConstructor
public class WebReadNotificationGateway implements ReadNotificationGateway {

    private final MittenteNotificationDetailsPage notificationDetailsPage;
    private final EntityStore entityStore;

    @Override
    public LegalNotificationDomain readNotification(String iun) {
        LegalNotificationDomain notification = readFromPortal(iun);
        entityStore.upsert(null, notification);
        return notification;
    }

    // solo lettura, senza registrare la notifica: per riconoscere quella appena inviata tra più candidate
    LegalNotificationDomain readFromPortal(String iun) {
        notificationDetailsPage.navigateTo(iun);
        MittenteNotificationSummarySection summary = notificationDetailsPage.notificationSummarySection();
        String statusLabel = notificationDetailsPage.notificationStatusSection().statusChip().text().read();
        return LegalNotificationDomain.builder()
                .iun(iun)
                .paProtocolNumber(summary.protocolNumberValue().read())
                .senderDenomination(summary.senderValue().read())
                .status(NotificationStatus.fromLabel(statusLabel).orElseThrow(() ->
                        new IllegalStateException("Stato della notifica non riconosciuto nel portale: " + statusLabel)))
                .build();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}
