package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.SendNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.ResolvedRecipient;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.web.mittente.infrastructure.page.NotificationSuccessPage;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Completa la procedura guidata (pagamenti dei destinatari, documento di test) e invia la notifica; la notifica si
 * ritrova tra quelle della PA dal numero di protocollo, poi si attende lo stato richiesto.
 * REFUSED non è osservabile dal portale: una notifica rifiutata non ha IUN e non compare tra le notifiche della PA.
 */
@Service
@RequiredArgsConstructor
public class WebSendNotificationGateway implements SendNotificationGateway {

    private static final Duration IUN_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration IUN_INTERVAL = Duration.ofSeconds(15);
    // stessi file di test che il canale BFF carica con il preload: documento e avviso pagoPA, metadati dell'F24
    private static final String TEST_DOCUMENT = "documents/test-attachment.pdf";
    private static final String TEST_F24_METADATA = "documents/f24-metadata.json";

    private final WebPresentationGateway browser;
    private final LegalNotificationCreationRequest request;
    private final WebSearchNotificationGateway searchNotificationGateway;
    private final WebReadNotificationGateway readNotificationGateway;
    private final WebWaitForNotificationStatusGateway waitForNotificationStatusGateway;

    @Override
    public void sendNotification(User sender, NotificationStatus targetStatus) {
        if (targetStatus == NotificationStatus.REFUSED) {
            throw new UnsupportedOperationException("Lo stato REFUSED non è osservabile dal portale della PA");
        }
        CreateNotificationPage page = browser.bind(CreateNotificationPage.class);
        page.continueToNextStep();
        compilePayments(page);
        page.documentazioneStep().fillFields(request.getSubject(), resourcePath(TEST_DOCUMENT));
        page.continueToNextStep();
        browser.bind(NotificationSuccessPage.class).assertLoaded();

        // la notifica compare tra quelle della PA solo dopo l'accettazione, quando ha lo IUN
        String iun = PollingUtils.pollUntil(
                this::findSentNotificationIun,
                Optional::isPresent,
                IUN_TIMEOUT,
                IUN_INTERVAL,
                "Notifica con numero di protocollo " + request.getPaProtocolNumber() + " non trovata tra quelle della PA")
                .orElseThrow();
        readNotificationGateway.readNotification(iun);
        waitForNotificationStatusGateway.waitForNotificationStatus(targetStatus);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    /**
     * Tra le notifiche del primo destinatario con lo stesso oggetto, quella con il numero di protocollo della request
     * (che si legge solo nel dettaglio); vuoto se non compare ancora tra le notifiche della PA.
     */
    private Optional<String> findSentNotificationIun() {
        return searchNotificationGateway.searchNotification(Map.of("recipientId", request.getRecipients().get(0).taxId()))
                .stream()
                .filter(notification -> request.getSubject().equals(notification.getSubject()))
                .map(LegalNotificationDomain::getIun)
                .filter(iun -> request.getPaProtocolNumber().equals(readNotificationGateway.readFromPortal(iun).getPaProtocolNumber()))
                .findFirst();
    }

    /**
     * "Posizione debitoria" e, se un destinatario ha dei pagamenti, "Dettaglio posizione debitoria". Con più
     * destinatari con pagamenti si caricherebbero gli stessi file di test più volte, che il portale non accetta.
     */
    private void compilePayments(CreateNotificationPage page) {
        List<ResolvedRecipient> recipientsWithPayments = request.getRecipients().stream()
                .filter(recipient -> !recipient.payments().isEmpty())
                .toList();
        if (recipientsWithPayments.size() > 1) {
            throw new UnsupportedOperationException("Più destinatari con pagamenti non sono supportati sul canale Web:"
                    + " il portale non accetta lo stesso file più di una volta");
        }
        page.dettaglioPosizioneDebitoriaStep().selectPaymentModels(request.getRecipients());
        page.continueToNextStep();
        if (!recipientsWithPayments.isEmpty()) {
            page.paymentDetailStep().fillFields(request.getFeePolicy(), recipientsWithPayments,
                    resourcePath(TEST_DOCUMENT), resourcePath(TEST_F24_METADATA));
            page.continueToNextStep();
        }
    }

    private static String resourcePath(String classpathResource) {
        try {
            return new ClassPathResource(classpathResource).getFile().getAbsolutePath();
        } catch (IOException e) {
            throw new UncheckedIOException("File di test non trovato: " + classpathResource, e);
        }
    }
}
