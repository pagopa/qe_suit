package it.pagopa.send.controller.notifica_legale;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.notification.domain.LegalNotificationDomain;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.common.journey.infrastructure.LegalNotificationJourneyImpl;
import it.pagopa.send.legalnotification.application.LegalNotificationUseCase;
import it.pagopa.send.model.LegalNotificationType;
import it.pagopa.send.model.RecipientSpec;
import it.pagopa.send.utils.IUNHelper;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LegalNotificationSteps {

    private final LegalNotificationJourneyImpl journey;
    private final LegalNotificationUseCase legalNotificationUseCase;
    private final WebBrowserContext webBrowserContext;
    private final NotificationContext notificationContext;

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

    private void createNotification(Tenant sender, LegalNotificationType type, String destinatarioName, Map<String, String> overrides) {
        Recipient recipient = Recipient.fromUsername(destinatarioName);

        journey.withSender(sender)
                .withType(type)
                .withRecipient(RecipientSpec.of(recipient))
                .withOverrides(overrides)
                .sendNotification(BffNotificationStatus.ACCEPTED);

        log.info("Request di notifica legale generata: {}", notificationContext.getBffNewNotificationResponse());
    }

    @Then("la notifica legale creata è in stato {string}")
    @Then("la richiesta di notifica è in stato {string}")
    public void assertRequestAccepted(String status) {
        BffNotificationStatus expectedStatus = BffNotificationStatus.valueOf(status.toUpperCase());
        Assertions.assertThat(notificationContext.getBffNewNotificationResponse()).isNotNull();

        //String iun = legalNotificationUseCase.extractIun(journey.getLastResponse());
        String iun = IUNHelper.extractFromBffNewNotificationResponse(notificationContext.getBffNewNotificationResponse());
        LegalNotificationDomain notification = legalNotificationUseCase.readNotification(iun);

        Assertions.assertThat(notification.getStatus()).isEqualTo(expectedStatus);
    }
}
