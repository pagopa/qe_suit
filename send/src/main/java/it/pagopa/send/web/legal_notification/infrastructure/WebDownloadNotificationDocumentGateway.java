package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.DownloadNotificationDocumentGateway;
import it.pagopa.send.common.legal_notification.domain.NotificationDocumentType;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Scarica un documento dal dettaglio della notifica nel portale della PA: l'atto dai documenti allegati, l'avviso di
 * avvenuta ricezione dalla sezione "Dettaglio della notifica" (si genera dopo il deposito: finché manca si riprova).
 */
@Service
@RequiredArgsConstructor
public class WebDownloadNotificationDocumentGateway implements DownloadNotificationDocumentGateway {

    private final MittenteNotificationDetailsPage notificationDetailsPage;
    private final WebPdfDownloader pdfDownloader;

    @Override
    public String downloadDocument(String iun, NotificationDocumentType type) {
        return pdfDownloader.download(() -> clickDocument(iun, type), "Il documento " + type + " della notifica " + iun);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    // dal dettaglio, il primo documento del tipo richiesto; se non c'è ancora si riprova al giro successivo
    private void clickDocument(String iun, NotificationDocumentType type) {
        notificationDetailsPage.navigateTo(iun);
        notificationDetailsPage.assertLoaded();
        List<Button> buttons = switch (type) {
            case ATTACHMENT -> notificationDetailsPage.attachmentSection().documentButtons();
            case AAR -> notificationDetailsPage.aarButtons();
        };
        buttons.stream().findFirst().ifPresent(Button::click);
    }
}
