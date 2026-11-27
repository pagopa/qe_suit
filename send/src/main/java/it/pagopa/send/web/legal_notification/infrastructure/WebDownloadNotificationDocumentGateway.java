package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.DownloadNotificationDocumentGateway;
import it.pagopa.send.common.legal_notification.domain.NotificationDocumentType;
import it.pagopa.send.web.infrastructure.WebBrowserLocation;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Scarica un documento dal dettaglio della notifica nel portale della PA. Il portale apre il PDF nella scheda del
 * browser, che lascia il portale per l'indirizzo del file: il documento è scaricato quando il browser è su un PDF. Se
 * il documento non è ancora disponibile (l'avviso di avvenuta ricezione si genera dopo il deposito, o il portale
 * avvisa di riprovare) si riapre il dettaglio e si ripete il click.
 */
@Service
@RequiredArgsConstructor
public class WebDownloadNotificationDocumentGateway implements DownloadNotificationDocumentGateway {

    private static final Duration DOWNLOAD_TIMEOUT = Duration.ofMinutes(2);
    private static final Duration DOWNLOAD_INTERVAL = Duration.ofSeconds(5);
    private static final String PDF_EXTENSION = ".pdf";

    private final MittenteNotificationDetailsPage notificationDetailsPage;
    private final WebBrowserLocation browserLocation;

    @Override
    public String downloadDocument(String iun, NotificationDocumentType type) {
        openDetail(iun);
        return PollingUtils.pollUntil(
                        () -> openedPdf().or(() -> {
                            clickDocument(iun, type);
                            return Optional.empty();
                        }),
                        Optional::isPresent,
                        DOWNLOAD_TIMEOUT,
                        DOWNLOAD_INTERVAL,
                        "Il documento " + type + " della notifica " + iun + " non si apre dal dettaglio della PA")
                .orElseThrow();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    private void openDetail(String iun) {
        notificationDetailsPage.navigateTo(iun);
        notificationDetailsPage.assertLoaded();
    }

    // il PDF aperto nel browser; vuoto finché il browser è sul portale
    private Optional<String> openedPdf() {
        String path = URI.create(browserLocation.currentUrl()).getPath();
        String fileName = path == null ? "" : String.valueOf(Path.of(path).getFileName());
        return fileName.toLowerCase().endsWith(PDF_EXTENSION) ? Optional.of(fileName) : Optional.empty();
    }

    // dal dettaglio, il primo documento del tipo richiesto; se non c'è ancora si riprova al giro successivo
    private void clickDocument(String iun, NotificationDocumentType type) {
        openDetail(iun);
        List<Button> buttons = switch (type) {
            case ATTACHMENT -> notificationDetailsPage.attachmentSection().documentButtons();
            case AAR -> notificationDetailsPage.aarButtons();
        };
        buttons.stream().findFirst().ifPresent(Button::click);
    }
}
