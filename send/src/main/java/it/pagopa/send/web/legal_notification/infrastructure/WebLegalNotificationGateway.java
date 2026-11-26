package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.LegalNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.web.mittente.infrastructure.page.NotificationSuccessPage;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
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
 * Crea la notifica dal portale della PA compilando la procedura guidata "Invia una nuova notifica", con gli stessi
 * dati di dominio del canale BFF; la sessione della PA mittente si apre alla navigazione, con l'utente corrente.
 * Le notifiche si leggono dal portale con {@link WebLegalNotificationReadGateway}.
 */
@Service
@RequiredArgsConstructor
public class WebLegalNotificationGateway implements LegalNotificationGateway {

    private static final Duration STATUS_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration STATUS_INTERVAL = Duration.ofSeconds(15);
    // stesso PDF di test che il canale BFF carica con il preload
    private static final String TEST_DOCUMENT = "documents/test-attachment.pdf";

    private final CreateNotificationPage createNotificationPage;
    private final WebRecipientFormMapper recipientFormMapper;
    private final NotificationSuccessPage notificationSuccessPage;
    private final MittenteNotificationDetailsPage notificationDetailsPage;
    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationCreationRequest request;
    private final WebLegalNotificationReadGateway readGateway;
    private final EntityStore entityStore;

    /** Compila "Informazioni preliminari" (default di dominio e campi del command) e passa a "Destinatari". */
    @Override
    public void prepareNotification(LegalNotificationCreationCommand command) {
        requestFactory.applyPreliminaryData(request, command);
        createNotificationPage.navigateTo();
        createNotificationPage.assertLoaded();
        createNotificationPage.fillPreliminaryInformation(request);
        createNotificationPage.continueToNextStep();
        command.getRecipients().forEach(this::addRecipient);
    }

    /** Compila il destinatario nel passo "Destinatari", in un nuovo blocco se il precedente è già compilato. */
    @Override
    public void addRecipient(RecipientSpec recipient) {
        if (!recipient.payments().isEmpty()) {
            throw new UnsupportedOperationException("I pagamenti dei destinatari non sono ancora supportati sul canale Web");
        }
        createNotificationPage.fillRecipient(recipientFormMapper.toForm(recipient));
    }

    /**
     * Completa la procedura guidata (nessun pagamento, documento di test) e invia la notifica; la notifica si ritrova
     * tra quelle della PA dal numero di protocollo, poi si attende lo stato richiesto.
     * REFUSED non è osservabile dal portale: una notifica rifiutata non ha IUN e non compare tra le notifiche della PA.
     */
    @Override
    public void sendNotification(User sender, NotificationStatus targetStatus) {
        if (targetStatus == NotificationStatus.REFUSED) {
            throw new UnsupportedOperationException("Lo stato REFUSED non è osservabile dal portale della PA");
        }
        String recipientTaxId = createNotificationPage.recipientsStep().recipientBoxes().get(0).taxId().read();
        createNotificationPage.continueToNextStep();
        createNotificationPage.debtPositionStep().selectNoPaymentForAllRecipients();
        createNotificationPage.continueToNextStep();
        createNotificationPage.attachmentsStep().fillFields(request.getSubject(), testDocumentPath());
        createNotificationPage.continueToNextStep();
        notificationSuccessPage.assertLoaded();

        // la notifica compare tra quelle della PA solo dopo l'accettazione, quando ha lo IUN
        String iun = PollingUtils.pollUntil(
                () -> readGateway.findIun(recipientTaxId, request.getSubject(), request.getPaProtocolNumber()),
                Optional::isPresent,
                STATUS_TIMEOUT,
                STATUS_INTERVAL,
                "Notifica con numero di protocollo " + request.getPaProtocolNumber() + " non trovata tra quelle della PA")
                .orElseThrow();
        readNotification(iun);
        waitForNotificationStatus(targetStatus);
    }

    /**
     * Attende lo stato dell'ultima notifica creata, letto dal dettaglio della notifica nel portale della PA.
     * ACCEPTED è raggiunto anche se la notifica è già oltre: dal portale la si legge solo quando compare tra le
     * notifiche della PA, e negli ambienti di test può essere già perfezionata.
     */
    @Override
    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        String iun = entityStore.getLastOrThrow(LegalNotificationDomain.class).getIun();
        PollingUtils.pollUntil(
                () -> readNotification(iun).getStatus(),
                status -> status == targetStatus || (targetStatus == NotificationStatus.ACCEPTED && isAccepted(status)),
                STATUS_TIMEOUT,
                STATUS_INTERVAL,
                String.format("La notifica %s non ha raggiunto lo stato %s", iun, targetStatus));
    }

    /** Annulla la notifica dal suo dettaglio nel portale della PA e attende che risulti annullata, come il canale BFF. */
    @Override
    public void deleteNotification(String iun) {
        notificationDetailsPage.navigateTo(iun);
        notificationDetailsPage.assertLoaded();
        notificationDetailsPage.cancelNotificationButton().click();
        notificationDetailsPage.confirmCancelNotificationButton().click();
        PollingUtils.pollUntil(
                () -> readNotification(iun).getStatus(),
                status -> status == NotificationStatus.CANCELLED,
                STATUS_TIMEOUT,
                STATUS_INTERVAL,
                String.format("La notifica %s non risulta annullata", iun));
    }

    /** Legge il dettaglio della notifica nel portale della PA e lo registra come ultima notifica, come il canale BFF. */
    @Override
    public LegalNotificationDomain readNotification(String iun) {
        LegalNotificationDomain notification = readGateway.read(iun);
        entityStore.upsert(null, notification);
        return notification;
    }

    /** Cerca tra le notifiche della PA con i filtri del portale ({@code iun}, {@code startDate}, ...). */
    @Override
    public List<LegalNotificationDomain> searchNotification(Map<String, String> overrides) {
        return readGateway.search(overrides);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    private static boolean isAccepted(NotificationStatus status) {
        return status != NotificationStatus.IN_VALIDATION && status != NotificationStatus.REFUSED;
    }

    private static String testDocumentPath() {
        try {
            return new ClassPathResource(TEST_DOCUMENT).getFile().getAbsolutePath();
        } catch (IOException e) {
            throw new UncheckedIOException("Documento di test non trovato: " + TEST_DOCUMENT, e);
        }
    }
}
