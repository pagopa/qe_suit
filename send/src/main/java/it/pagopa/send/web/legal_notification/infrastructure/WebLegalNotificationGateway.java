package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.application.context.EntityStore;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.mittente.infrastructure.page.NotificationSuccessPage;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.section.MittenteNotificationSummarySection;
import it.pagopa.utils.async.PollingUtils;
import org.springframework.core.io.ClassPathResource;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.application.LegalNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Crea la notifica dal portale della PA compilando la procedura guidata "Invia una nuova notifica", con gli stessi
 * dati di dominio del canale BFF; la sessione della PA mittente si apre alla navigazione, con l'utente corrente.
 */
@Service
@RequiredArgsConstructor
public class WebLegalNotificationGateway implements LegalNotificationGateway {

    private static final Duration STATUS_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration STATUS_INTERVAL = Duration.ofSeconds(15);
    // stesso PDF di test che il canale BFF carica con il preload
    private static final String TEST_DOCUMENT = "documents/test-attachment.pdf";
    private static final Pattern IUN_PATTERN = Pattern.compile("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d");

    private final WebPresentationGateway browser;
    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationCreationRequest request;
    private final EntityStore entityStore;

    /** Compila "Informazioni preliminari" (default di dominio e campi del command) e passa a "Destinatari". */
    @Override
    public void prepareNotification(LegalNotificationCreationCommand command) {
        requestFactory.applyPreliminaryData(request, command);
        CreateNotificationPage page = browser.bind(CreateNotificationPage.class);
        page.navigateTo();
        page.assertLoaded();
        page.compileInformazioniPreliminari(request);
        page.continueToNextStep();
        command.getRecipients().forEach(this::addRecipient);
    }

    /** Compila il destinatario nel passo "Destinatari", in un nuovo blocco se il precedente è già compilato. */
    @Override
    public void addRecipient(RecipientSpec recipient) {
        if (!recipient.payments().isEmpty()) {
            throw new UnsupportedOperationException("I pagamenti dei destinatari non sono ancora supportati sul canale Web");
        }
        browser.bind(CreateNotificationPage.class).compileDestinatario(recipient);
    }

    /**
     * Completa la procedura guidata (nessun pagamento, documento di test) e invia la notifica; lo IUN si ricava dalle
     * notifiche della PA, riconoscendo la notifica dal numero di protocollo, poi si attende lo stato richiesto.
     * REFUSED non è osservabile dal portale: una notifica rifiutata non ha IUN e non compare tra le notifiche della PA.
     */
    @Override
    public void sendNotification(User sender, NotificationStatus targetStatus) {
        if (targetStatus == NotificationStatus.REFUSED) {
            throw new UnsupportedOperationException("Lo stato REFUSED non è osservabile dal portale della PA");
        }
        CreateNotificationPage page = browser.bind(CreateNotificationPage.class);
        String recipientTaxId = page.destinatariStep().recipientBoxes().get(0).taxId().read();
        page.continueToNextStep();
        page.dettaglioPosizioneDebitoriaStep().selectNoPaymentForAllRecipients();
        page.continueToNextStep();
        page.documentazioneStep().fillFields(request.getSubject(), testDocumentPath());
        page.continueToNextStep();
        browser.bind(NotificationSuccessPage.class).assertLoaded();

        entityStore.upsert(null, LegalNotificationDomain.builder()
                .iun(findSentNotificationIun(recipientTaxId))
                .subject(request.getSubject())
                .build());
        waitForNotificationStatus(targetStatus);
    }

    /**
     * Attende lo stato dell'ultima notifica creata, letto dal dettaglio della notifica nel portale della PA.
     * ACCEPTED è raggiunto anche se la notifica è già oltre: dal portale la si legge solo quando compare tra le
     * notifiche della PA, e negli ambienti di test può essere già perfezionata.
     */
    @Override
    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        LegalNotificationDomain notification = entityStore.getLastOrThrow(LegalNotificationDomain.class);
        MittenteNotificationDetailsPage details = browser.bind(MittenteNotificationDetailsPage.class);
        NotificationStatus reached = PollingUtils.pollUntil(
                () -> {
                    details.navigateTo(notification.getIun());
                    return NotificationStatusLabel.toStatus(details.notificationStatusSection().statusChip().text().read());
                },
                status -> status == targetStatus || (targetStatus == NotificationStatus.ACCEPTED && isAccepted(status)),
                STATUS_TIMEOUT,
                STATUS_INTERVAL,
                String.format("La notifica %s non ha raggiunto lo stato %s", notification.getIun(), targetStatus));
        entityStore.upsert(null, notification.toBuilder().status(reached).build());
    }

    private static boolean isAccepted(NotificationStatus status) {
        return status != NotificationStatus.IN_VALIDATION && status != NotificationStatus.REFUSED;
    }

    @Override
    public void deleteNotification(String iun) {
        throw new UnsupportedOperationException("deleteNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public LegalNotificationDomain readNotification(String iun) {
        throw new UnsupportedOperationException("readNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public List<LegalNotificationDomain> searchNotification(Map<String, String> overrides) {
        throw new UnsupportedOperationException("searchNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    // la notifica compare tra quelle della PA solo dopo l'accettazione, quando ha lo IUN
    private String findSentNotificationIun(String recipientTaxId) {
        DashboardPage dashboard = browser.bind(DashboardPage.class);
        return PollingUtils.pollUntil(
                () -> {
                    dashboard.navigateTo();
                    dashboard.assertLoaded();
                    dashboard.searchNotification(Map.of("recipientId", recipientTaxId));
                    if (dashboard.notificationsTable().tBody().rows().isEmpty()) {
                        return Optional.<String>empty();
                    }
                    dashboard.goToNotificationDetails();
                    MittenteNotificationSummarySection summary =
                            browser.bind(MittenteNotificationDetailsPage.class).notificationSummarySection();
                    return request.getPaProtocolNumber().equals(summary.protocolNumberValue().read())
                            ? Optional.of(extractIun(summary.iunHeader().read()))
                            : Optional.<String>empty();
                },
                Optional::isPresent,
                STATUS_TIMEOUT,
                STATUS_INTERVAL,
                "Notifica con numero di protocollo " + request.getPaProtocolNumber() + " non trovata tra quelle della PA")
                .orElseThrow();
    }

    private static String extractIun(String iunHeader) {
        Matcher matcher = IUN_PATTERN.matcher(iunHeader);
        if (!matcher.find()) {
            throw new IllegalStateException("IUN non presente nel titolo del dettaglio: " + iunHeader);
        }
        return matcher.group();
    }

    private static String testDocumentPath() {
        try {
            return new ClassPathResource(TEST_DOCUMENT).getFile().getAbsolutePath();
        } catch (IOException e) {
            throw new UncheckedIOException("Documento di test non trovato: " + TEST_DOCUMENT, e);
        }
    }
}
