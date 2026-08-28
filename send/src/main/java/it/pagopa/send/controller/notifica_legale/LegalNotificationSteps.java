package it.pagopa.send.controller.notifica_legale;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.common.kernel.domain.Recipient;
import it.pagopa.send.common.kernel.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.legalnotification.application.LegalNotificationJourney;
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

    private final LegalNotificationJourney journey;
    private final LegalNotificationUseCase legalNotificationUseCase;
    private final WebBrowserContext webBrowserContext;

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
    public void enteCreaNotificaConOverride(String enteName, LegalNotificationType type, String destinatarioName, DataTable overrides) {
        createNotification(Tenant.fromOrganization(enteName), type, destinatarioName, overrides.asMap(String.class, String.class));
    }

    private void createNotification(Tenant sender, LegalNotificationType type, String destinatarioName, Map<String, String> overrides) {
        Recipient recipient = Recipient.fromUsername(destinatarioName);

        journey.withSender(sender)
                .withType(type)
                .withRecipient(RecipientSpec.of(recipient))
                .withOverrides(overrides)
                .send();

        log.info("Request di notifica legale generata: {}", journey.getLastRequest());
    }

    @Then("la richiesta di notifica è stata accettata")
    public void assertRequestAccepted() {
        Assertions.assertThat(journey.getLastResponse()).isNotNull();

        //String iun = legalNotificationUseCase.extractIun(journey.getLastResponse());
        String iun = IUNHelper.extractFromBffNewNotificationResponse(journey.getLastResponse());
        BffFullNotificationV1 notification = legalNotificationUseCase.waitForStatus(iun, BffNotificationStatus.ACCEPTED);

        Assertions.assertThat(notification.getNotificationStatus()).isEqualTo(BffNotificationStatus.ACCEPTED);
    }
}
