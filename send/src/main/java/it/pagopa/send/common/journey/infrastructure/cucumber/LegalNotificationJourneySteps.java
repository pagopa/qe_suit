package it.pagopa.send.common.journey.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationType;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LegalNotificationJourneySteps {

    private final SendJourney sendJourney;
    private final RecipientSpecFactory recipientSpecFactory;
    private final UserProvider userProvider;
    private final CurrentUserSession currentUserSession;
    private final EntityStore entityStore;

    @Given("una notifica di tipo {legalNotificationType} creata dalla PA {user} per il destinatario {user} in stato {string}")
    public void createLegalNotification(LegalNotificationType type, User sender, User recipient, String targetStatus) {
        RecipientSpec recipientSpec = recipientSpecFactory.build(recipient, type.recipientOverrides());
        sendJourney
                .prepareNotification(Map.of())
                .withRecipient(recipientSpec)
                .sendNotification(sender, NotificationStatus.fromValue(targetStatus));
    }

    @Given("la PA {user} ha creato ed annullato una notifica di tipo {legalNotificationType} per il destinatario {user}")
    public void createAndCancelLegalNotification(User sender, LegalNotificationType type, User recipient) {
        RecipientSpec recipientSpec = recipientSpecFactory.build(recipient, type.recipientOverrides());
        sendJourney
                .prepareNotification(Map.of())
                .withRecipient(recipientSpec)
                .sendNotification(sender, NotificationStatus.ACCEPTED)
                .deleteNotification();
    }

    /**
     * Step "tutto-in-uno" per una notifica multi-destinatario con due destinatari fissi
     * (Lucrezia e Petrarca), utile per scenari che non hanno bisogno di destinatari/dati
     * configurabili. Per scenari che richiedono destinatari/dati variabili, usare invece gli step
     * configurabili di {@link it.pagopa.send.common.legal_notification.infrastructure.cucumber.LegalNotificationSteps}.
     */
    @Given("una notifica di tipo multidestinatario creata dalla PA {user}")
    public void createMultiRecipientNotification(User sender) {
        RecipientSpec lucrezia = recipientSpecFactory.build(userProvider.resolve(UserAlias.USER_1), Map.of(
                "physicalAddress_address", "Via Ok 890",
                "physicalAddress_municipality", "Palermo",
                "physicalAddress_province", "PA",
                "physicalAddress_zip", "90100",
                "pagoPA_number", "0",
                "F24_number", "0"
        ));

        // IMPRESA_1: stesso codice fiscale numerico (12666810299) del destinatario PG usato finora
        RecipientSpec petrarca = recipientSpecFactory.build(userProvider.resolve(UserAlias.IMPRESA_1), Map.of(
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

    @When("l'ente {user} crea una notifica di tipo {legalNotificationType} per il destinatario {user}")
    public void createNotificationBySender(User sender, LegalNotificationType type, User recipient) {
        createNotification(sender, type, recipient, Map.of());
    }

    @When("l'ente crea una notifica di tipo {legalNotificationType} per il destinatario {user}")
    public void createNotificationBySessionSender(LegalNotificationType type, User recipient) {
        createNotification(currentUserSession.getUser(), type, recipient, Map.of());
    }

    @When("l'ente {user} crea una notifica di tipo {legalNotificationType} per il destinatario {user} con i seguenti valori:")
    public void createNotificationWithRecipientValues(User sender, LegalNotificationType type, User recipient, DataTable overrides) {
        createNotification(sender, type, recipient, overrides.asMap(String.class, String.class));
    }

    @When("viene ricercata e aperta una notifica che rispetta i seguenti criteri:")
    public void searchAndReadLegalNotification(Map<String, String> criteria) {
        sendJourney
                .prepareNotification(criteria)
                .readNotification();
    }

    private void createNotification(User sender, LegalNotificationType type, User recipient, Map<String, String> recipientOverrides) {
        Map<String, String> overrides = new HashMap<>(type.recipientOverrides());
        overrides.putAll(recipientOverrides);
        sendJourney
                .prepareNotification(Map.of())
                .withRecipient(recipientSpecFactory.build(recipient, overrides))
                .sendNotification(sender, NotificationStatus.ACCEPTED);

        log.info("Notifica legale generata: {}", entityStore.getLastOrThrow(LegalNotificationDomain.class));
    }
}
