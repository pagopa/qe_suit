package it.pagopa.send.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.send.common.informal_notification.application.InformalNotificationUseCase;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalRecipientSpecFactory;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
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

    @Given("una comunicazione bonaria di campagna {string} creata dalla PA {user} per {user}")
    public void createInformalNotificationAllInOne(String campaignId, User sender, User recipient) {
        InformalRecipientSpec recipientSpec = informalRecipientSpecFactory.build(recipient, Map.of());
        currentUserSession.setUser(sender);
        sendJourney
                .withInformalSender(sender)
                .prepareInformalNotification(Map.of("campaignId", campaignId))
                .withInformalRecipient(recipientSpec)
                .sendInformalNotification(sender);
    }

    @Given("la PA {user} predispone una nuova comunicazione bonaria con i seguenti dati:")
    public void prepareInformalNotification(User tenant, Map<String, String> data) {
        currentUserSession.setUser(tenant);
        informalNotificationUseCase.prepareNotification(data);
    }

    @Given("viene aggiunto {user} come destinatario della comunicazione bonaria con i seguenti dati:")
    public void addRecipientToInformalNotification(User recipient, Map<String, String> data) {
        // il mittente è l'utente impostato dallo step "la PA ... predispone una nuova comunicazione bonaria"
        User sender = currentUserSession.getUser();
        InformalRecipientSpec spec = informalRecipientSpecFactory.build(recipient, data);
        informalNotificationUseCase.addRecipient(sender, spec);
    }

    @When("la comunicazione bonaria viene inviata dalla PA {user}")
    public void sendInformalNotification(User sender) {
        currentUserSession.setUser(sender);
        informalNotificationUseCase.sendNotification(sender);
    }
}
