package it.pagopa.send.web.legal_notification.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.journey.infrastructure.LegalNotificationJourneyImpl;
import it.pagopa.send.common.legal_notification.application.LegalNotificationUseCase;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationType;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
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
    private final WebBrowserContext webBrowserContext;
    private final EntityStore entityStore;

    @When("l'ente {string} crea una notifica di tipo {legalNotificationType} per il destinatario {string}")
    public void enteCreaNotifica(String enteName, LegalNotificationType type, String destinatarioName) {
        createNotification(Tenant.fromOrganization(enteName), type, destinatarioName, Map.of());
    }

    @When("l'ente crea una notifica di tipo {legalNotificationType} per il destinatario {string}")
    public void enteContestoCreaNotifica(LegalNotificationType type, String destinatarioName) {
        Tenant tenant = webBrowserContext.getTenant();
        if (tenant == null) {
            throw new IllegalStateException("Nessun ente autenticato nel contesto: specifica l'ente esplicitamente oppure effettua prima il login");
        }
        createNotification(tenant, type, destinatarioName, Map.of());
    }

    @When("l'ente {string} crea una notifica di tipo {legalNotificationType} per il destinatario {string} con i seguenti valori:")
    public void createNotificationWithOverride(String enteName, LegalNotificationType type, String destinatarioName, DataTable overrides) {
        createNotification(Tenant.fromOrganization(enteName), type, destinatarioName, overrides.asMap(String.class, String.class));
    }

    private void createNotification(Tenant sender, LegalNotificationType type, String destinatarioName, Map<String, String> recipientOverrides) {
        Recipient recipient = Recipient.fromUsername(destinatarioName);

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
