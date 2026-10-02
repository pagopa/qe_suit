package it.pagopa.send.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.send.common.informal_notification.application.InformalNotificationUseCase;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalRecipientSpecFactory;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class InformalNotificationJourneySteps {

    private final SendJourney sendJourney;
    private final InformalNotificationUseCase informalNotificationUseCase;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;
    private final CurrentUserSession currentUserSession;

    @Given("una comunicazione bonaria di campagna {string} creata dalla PA {tenant} per {recipient}")
    public void createInformalNotificationAllInOne(String campaignId, Tenant sender, Recipient recipient) {
        InformalRecipientSpec recipientSpec = informalRecipientSpecFactory.build(recipient, Map.of());
        currentUserSession.setSender(sender);
        sendJourney
                .withInformalSender(sender)
                .prepareInformalNotification(Map.of("campaignId", campaignId))
                .withInformalRecipient(recipientSpec)
                .sendInformalNotification(sender);
    }

    @Given("la PA {tenant} predispone una nuova comunicazione bonaria con i seguenti dati:")
    public void prepareInformalNotification(Tenant tenant, Map<String, String> data) {
        currentUserSession.setSender(tenant);
        informalNotificationUseCase.prepareNotification(data);
    }

    @Given("viene aggiunto {recipient} come destinatario della comunicazione bonaria con i seguenti dati:")
    public void addRecipientToInformalNotification(Recipient recipient, Map<String, String> data) {
        Tenant sender = currentUserSession.getSender();
        if (sender == null) {
            throw new IllegalStateException("Nessun mittente impostato nella sessione prima di aggiungere il destinatario.");
        }
        InformalRecipientSpec spec = informalRecipientSpecFactory.build(recipient, data);
        informalNotificationUseCase.addRecipient(sender, spec);
    }

    @When("la comunicazione bonaria viene inviata dalla PA {tenant}")
    public void sendInformalNotification(Tenant sender) {
        currentUserSession.setSender(sender);
        informalNotificationUseCase.sendNotification(sender);
    }
}
