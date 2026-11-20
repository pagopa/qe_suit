package it.pagopa.send.common.legal_notification.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.legal_notification.application.LegalNotificationUseCase;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.Map;

/**
 * Step configurabili della notifica legale, da comporre nello scenario quando contano i dati della notifica o dei
 * destinatari: predisposizione, aggiunta di un destinatario (ripetibile), invio e attesa dello stato, che passano dal Journey come gli step
 * già pronti di {@link it.pagopa.send.common.journey.infrastructure.cucumber.LegalNotificationJourneySteps}, e verifica
 * dello stato dell'ultima notifica creata, che la legge con lo UseCase.
 */
@RequiredArgsConstructor
public class LegalNotificationSteps {

    private final SendJourney sendJourney;
    private final RecipientSpecFactory recipientSpecFactory;
    private final CurrentUserSession currentUserSession;
    private final LegalNotificationUseCase legalNotificationUseCase;
    private final EntityStore entityStore;

    /** Predispone i dati base della notifica e imposta la PA indicata come mittente. */
    @Given("la PA {user} predispone una nuova notifica con i seguenti dati:")
    public void prepareNotification(User sender, Map<String, String> data) {
        sendJourney.withSender(sender).prepareNotification(data);
    }

    /**
     * Aggiunge un destinatario censito alla notifica in preparazione, con i valori di default (1 pagoPA + 1 F24,
     * indirizzo fisico di default) sovrascrivibili puntualmente; ripetibile una volta per destinatario.
     */
    @Given("è stato aggiunto {user} come destinatario:")
    public void addRecipientToNotification(User recipient, Map<String, String> data) {
        sendJourney.withRecipient(recipientSpecFactory.build(recipient, data));
    }

    /** Invia la notifica predisposta per il mittente della sessione: l'invio arriva al massimo ad ACCEPTED. */
    @When("la notifica viene sottomessa")
    public void submitNotification() {
        sendJourney.sendNotification(currentUserSession.getUser(), NotificationStatus.ACCEPTED);
    }

    /** Attende lo stato dell'ultima notifica, creata con gli step standard o con uno step che usa il Command. */
    @And("la notifica raggiunge lo stato {string}")
    public void waitForNotificationStatus(String targetStatus) {
        sendJourney.waitForNotificationStatus(NotificationStatus.fromValue(targetStatus));
    }

    @Then("la notifica legale creata è in stato {string}")
    @Then("la richiesta di notifica è in stato {string}")
    public void assertNotificationStatus(String status) {
        NotificationStatus expectedStatus = NotificationStatus.valueOf(status.toUpperCase());
        LegalNotificationDomain created = entityStore.getLastOrThrow(LegalNotificationDomain.class);

        LegalNotificationDomain notification = legalNotificationUseCase.readNotification(created.getIun());

        Assertions.assertThat(notification.getStatus()).isEqualTo(expectedStatus);
    }
}
