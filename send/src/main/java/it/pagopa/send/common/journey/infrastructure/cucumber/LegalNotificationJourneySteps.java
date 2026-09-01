package it.pagopa.send.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.model.LegalNotificationType;
import it.pagopa.send.model.RecipientSpec;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class LegalNotificationJourneySteps {

    private final SendJourney sendJourney;

    @Given("una notifica di tipo {legalNotificationType} creata dalla PA {tenant} per il destinatario {string} in stato {string}")
    public void createLegalNotification(LegalNotificationType type, Tenant sender, String recipient, String targetStatus) {
        sendJourney
                .withSender(sender)
                .withType(type)
                .withRecipient(RecipientSpec.of(Recipient.fromUsername(recipient)))
                .sendNotification(BffNotificationStatus.fromValue(targetStatus));
    }

    @Given("la PA {tenant} ha creato ed annullato una notifica di tipo {legalNotificationType} per il destinatario {string}")
    public void createAndCancelLegalNotification(Tenant sender, LegalNotificationType type, String recipient) {
        sendJourney
                .withSender(sender)
                .withType(type)
                .withRecipient(RecipientSpec.of(Recipient.fromUsername(recipient)))
                .sendNotification(BffNotificationStatus.ACCEPTED)
                .deleteNotification();
    }

    @Given("viene ricercata e aperta una notifica che rispetta i seguenti criteri:")
    public void searchAndReadLegalNotification(Map<String, String> criteria) {
        sendJourney
                .withOverrides(criteria)
                .readNotification();
    }
}
