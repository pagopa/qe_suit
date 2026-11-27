package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.ReadReceiptNoticesGateway;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Legge gli avvisi di avvenuta ricezione dalla sezione "Dettaglio della notifica" del portale della PA: l'etichetta di
 * ogni avviso riporta il destinatario.
 */
@Service
@RequiredArgsConstructor
public class WebReadReceiptNoticesGateway implements ReadReceiptNoticesGateway {

    private final MittenteNotificationDetailsPage notificationDetailsPage;

    @Override
    public List<String> readReceiptNotices(String iun) {
        notificationDetailsPage.navigateTo(iun);
        notificationDetailsPage.assertLoaded();
        return notificationDetailsPage.aarLabels().stream()
                .map(Readable::read)
                .toList();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}
