package it.pagopa.send.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.application.LegalNotificationUseCase;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationType;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class LegalNotificationJourneySteps {

    private final SendJourney sendJourney;
    private final LegalNotificationUseCase legalNotificationUseCase;
    private final RecipientSpecFactory recipientSpecFactory;
    private final CurrentChannel<Channel> currentChannel;
    private final CurrentUserSession currentUserSession;

    @Given("una notifica di tipo {legalNotificationType} creata dalla PA {tenant} per il destinatario {string} in stato {string}")
    public void createLegalNotification(LegalNotificationType type, Tenant sender, String recipient, String targetStatus) {
        RecipientSpec recipientSpec = recipientSpecFactory.build(Recipient.fromUsername(recipient), type.recipientOverrides());
        sendJourney
                .prepareNotification(Map.of())
                .withRecipient(recipientSpec)
                .sendNotification(sender, NotificationStatus.fromValue(targetStatus));
    }

    @Given("la PA {tenant} ha creato ed annullato una notifica di tipo {legalNotificationType} per il destinatario {string}")
    public void createAndCancelLegalNotification(Tenant sender, LegalNotificationType type, String recipient) {
        RecipientSpec recipientSpec = recipientSpecFactory.build(Recipient.fromUsername(recipient), type.recipientOverrides());
        sendJourney
                .prepareNotification(Map.of())
                .withRecipient(recipientSpec)
                .sendNotification(sender, NotificationStatus.ACCEPTED)
                .deleteNotification();
    }

    /**
     * Step "tutto-in-uno" per una notifica multi-destinatario con due destinatari fissi
     * (Lucrezia e Petrarca), utile per scenari che non hanno bisogno di destinatari/dati
     * configurabili. Per scenari che richiedono destinatari/dati variabili, usare invece i tre
     * step separati ({@link #prepareNotification}, {@link #addRecipientToNotification},
     * {@link #sendNotificationViaBff}).
     */
    @Given("una notifica di tipo multidestinatario creata dalla PA {tenant}")
    public void createMultiRecipientNotification(Tenant sender) {
        RecipientSpec lucrezia = recipientSpecFactory.build(Recipient.LUCREZIA, Map.of(
                "physicalAddress_address", "Via Ok 890",
                "physicalAddress_municipality", "Palermo",
                "physicalAddress_province", "PA",
                "physicalAddress_zip", "90100",
                "pagoPA_number", "0",
                "F24_number", "0"
        ));

        RecipientSpec petrarca = recipientSpecFactory.build(Recipient.PETRARCA, Map.of(
                "digitalDomicile", "test@pec.it",
                "pagoPA_number", "1",
                "F24_number", "0"
        ));

        sendJourney
                .withSender(sender)
                .prepareNotification(Map.of(
                        "subject", "Comunicazione di test multi-destinatario",
                        "physicalCommunication", "REGISTERED_LETTER_890",
                        "feePolicy", "FLAT_RATE"
                ))
                .withRecipient(lucrezia)
                .withRecipient(petrarca)
                .sendNotification(sender, NotificationStatus.ACCEPTED);
    }

    @Given("viene ricercata e aperta una notifica che rispetta i seguenti criteri:")
    public void searchAndReadLegalNotification(Map<String, String> criteria) {
        sendJourney
                .prepareNotification(criteria)
                .readNotification();
    }

    /**
     * Primo step del flusso di creazione notifica multi-destinatario: compila i campi base della
     * notifica (mittente escluso: viene impostato dallo step di invio). Agnostico dal channel;
     * ripetibile in combinazione con {@link #addRecipientToNotification} e concluso da
     * {@link #sendNotificationViaBff}. A differenza degli step sopra, che compongono un unico
     * step tramite {@link SendJourney}, questi tre step chiamano {@link LegalNotificationUseCase}
     * direttamente: devono per forza restare su step Cucumber separati.
     */
    @Given("la PA {tenant} predispone una nuova notifica con i seguenti dati:")
    public void prepareNotification(Tenant tenant, Map<String, String> data) {
        currentUserSession.setSender(tenant);
        legalNotificationUseCase.prepareNotification(data);
    }

    /**
     * Secondo step, ripetibile una volta per destinatario: aggiunge un destinatario censito
     * ({@link Recipient}) alla notifica in preparazione, con default sensati (1 pagoPA + 1 F24,
     * indirizzo fisico di default) sovrascrivibili puntualmente da {@link RecipientSpecFactory}.
     */
    @Given("viene aggiunto {recipient} come destinatario con i seguenti dati:")
    public void addRecipientToNotification(Recipient recipient, Map<String, String> data) {
        RecipientSpec recipientSpec = recipientSpecFactory.build(recipient, data);
        legalNotificationUseCase.addRecipient(recipientSpec);
    }

    /**
     * Terzo e ultimo step: invia la notifica predisposta tramite il canale BFF e attende che
     * raggiunga lo stato indicato. Quando verrà implementato il canale WEB si aggiungerà un
     * secondo metodo analogo con testo letterale "tramite interfaccia web".
     */
    @When("la notifica viene sottomessa e si attende che lo stato diventi {string}")
    public void sendNotificationViaBff(Tenant sender, String targetStatus) {
        legalNotificationUseCase.sendNotification(sender, NotificationStatus.fromValue(targetStatus));
    }
}
