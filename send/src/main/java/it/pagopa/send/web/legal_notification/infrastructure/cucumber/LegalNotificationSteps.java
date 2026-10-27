package it.pagopa.send.web.legal_notification.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.journey.infrastructure.LegalNotificationJourneyImpl;
import it.pagopa.send.common.legal_notification.application.LegalNotificationUseCase;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationType;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LegalNotificationSteps {

    private final LegalNotificationJourneyImpl journey;
    private final LegalNotificationUseCase legalNotificationUseCase;
    private final RecipientSpecFactory recipientSpecFactory;
    private final CurrentUserSession currentUserSession;
    private final EntityStore entityStore;

    @When("l'ente {user} crea una notifica di tipo {legalNotificationType} per il destinatario {user}")
    public void enteCreaNotifica(User sender, LegalNotificationType type, User recipient) {
        createNotification(sender, type, recipient, Map.of());
    }

    @When("l'ente crea una notifica di tipo {legalNotificationType} per il destinatario {user}")
    public void enteContestoCreaNotifica(LegalNotificationType type, User recipient) {
        createNotification(currentUserSession.getSender(), type, recipient, Map.of());
    }

    @When("l'ente {user} crea una notifica di tipo {legalNotificationType} per il destinatario {user} con i seguenti valori:")
    public void createNotificationWithOverride(User sender, LegalNotificationType type, User recipient, DataTable overrides) {
        createNotification(sender, type, recipient, overrides.asMap(String.class, String.class));
    }

    private void createNotification(User sender, LegalNotificationType type, User recipient, Map<String, String> recipientOverrides) {
        Map<String, String> overrides = new HashMap<>(type.recipientOverrides());
        overrides.putAll(recipientOverrides);
        RecipientSpec recipientSpec = recipientSpecFactory.build(recipient, overrides);

        journey.prepareNotification(Map.of())
                .withRecipient(recipientSpec)
                .sendNotification(sender, NotificationStatus.ACCEPTED);

        log.info("Notifica legale generata: {}", entityStore.getLastOrThrow(LegalNotificationDomain.class));
    }

    @Then("la notifica legale creata è in stato {string}")
    @Then("la richiesta di notifica è in stato {string}")
    public void assertRequestAccepted(String status) {
        NotificationStatus expectedStatus = NotificationStatus.valueOf(status.toUpperCase());

        LegalNotificationDomain created = entityStore.getLastOrThrow(LegalNotificationDomain.class);
        Assertions.assertThat(created).isNotNull();

        LegalNotificationDomain notification = legalNotificationUseCase.readNotification(created.getIun());

        Assertions.assertThat(notification.getStatus()).isEqualTo(expectedStatus);
    }
}
