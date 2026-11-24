package it.pagopa.send.common.legal_notification.infrastructure.cucumber;

import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationCreationCommandFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.legal_notification.application.LegalNotificationUseCase;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationRequestOutcome;
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
    private final LegalNotificationCreationCommandFactory commandFactory;
    private final CurrentUserSession currentUserSession;
    private final LegalNotificationUseCase legalNotificationUseCase;
    private final EntityStore entityStore;

    /** Predispone i dati base della notifica e imposta la PA indicata come mittente. */
    @Given("la PA {user} predispone una nuova notifica con i seguenti dati:")
    public void prepareNotification(User sender, Map<String, String> data) {
        sendJourney.prepareNotification(commandFactory.fromNotificationData(sender, data));
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

    /** Invia la notifica predisposta con dati non conformi: la validazione la deve rifiutare (REFUSED). */
    @When("la notifica non conforme viene sottomessa")
    public void submitNonCompliantNotification() {
        sendJourney.sendNotification(currentUserSession.getUser(), NotificationStatus.REFUSED);
    }

    /** Attende lo stato dell'ultima notifica, creata con gli step standard o con uno step che usa il Command. */
    @And("la notifica raggiunge lo stato {notificationStatus}")
    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        sendJourney.waitForNotificationStatus(targetStatus);
    }

    /**
     * L'ultima notifica creata ha superato la validazione, letta dal canale dello step: accettata o già oltre
     * (consegna, perfezionamento, ...), perché dopo l'accettazione lo stato può avanzare prima della lettura.
     */
    @Then("la notifica risulta accettata")
    public void assertNotificationAccepted() {
        LegalNotificationDomain created = entityStore.getLastOrThrow(LegalNotificationDomain.class);
        NotificationStatus status = legalNotificationUseCase.readNotification(created.getIun()).getStatus();
        Assertions.assertThat(status.isAccepted())
                .as("La notifica %s non risulta accettata: stato %s", created.getIun(), status)
                .isTrue();
    }

    /** La richiesta dell'ultima notifica sottomessa è stata rifiutata, con il codice d'errore che ne è la causa. */
    @Then("la richiesta di notifica risulta rifiutata con l'errore {string}")
    public void assertRequestRefused(String errorCode) {
        LegalNotificationRequestOutcome outcome = entityStore.getLastOrThrow(LegalNotificationRequestOutcome.class);
        Assertions.assertThat(outcome.status()).isEqualTo(NotificationStatus.REFUSED);
        Assertions.assertThat(outcome.errorCodes()).contains(errorCode);
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
