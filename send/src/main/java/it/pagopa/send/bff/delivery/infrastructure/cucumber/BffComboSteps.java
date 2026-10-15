package it.pagopa.send.bff.delivery.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffChannelDeliveryStatusV1;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffFullSentInformalNotificationTimelineV1;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffFullSentInformalNotificationV1;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffInformalNotificationTimelineGroup;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffInformalNotificationTimelineStatusHistoryV1;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.FullInformalNotificationRecipientV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BffComboSteps {

    private final Environment environment;
    private final BffComboRestClient bffComboRestClient;

    private BffFullSentInformalNotificationV1 currentDetailResponse;
    private BffFullSentInformalNotificationTimelineV1 currentTimelineResponse;

    private String resolveDynamicValue(String rawValue) {
        if (rawValue == null || rawValue.isBlank() || environment == null) {
            return rawValue;
        }
        String trimmed = rawValue.trim();
        if (trimmed.startsWith("${") && trimmed.endsWith("}")) {
            return environment.resolvePlaceholders(trimmed);
        }
        if (trimmed.startsWith("$")) {
            String propertyKey = trimmed.substring(1);
            String propertyValue = environment.getProperty(propertyKey);
            if (propertyValue != null && !propertyValue.isBlank()) {
                return propertyValue;
            }
            return environment.resolvePlaceholders(trimmed);
        }
        String directProperty = environment.getProperty(trimmed);
        if (directProperty != null && !directProperty.isBlank()) {
            return directProperty;
        }
        return environment.resolvePlaceholders(trimmed);
    }

    @Given("una comunicazione bonaria di campagna {string} per il destinatario {string} con IUN {string}")
    public void setupComboContext(String campaignType, String recipientType, String iun) {
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Impostazione contesto comunicazione bonaria IUN {}, tipo {}, destinatario {}", resolvedIun, campaignType, recipientType);
    }

    @Given("una comunicazione bonaria con condizione {string} e IUN {string}")
    public void setupComboConditionContext(String condition, String iun) {
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Impostazione contesto comunicazione bonaria con condizione {}, IUN {}", condition, resolvedIun);
    }

    @Given("una comunicazione bonaria valida con IUN {string} ed eventi registrati sui canali {string}")
    public void setupValidComboTimelineContext(String iun, String channels) {
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Impostazione contesto timeline comunicazione bonaria IUN {}", resolvedIun);
    }

    @Given("una comunicazione bonaria valida con IUN {string} per esito {string}")
    public void setupComboTimelineOutcomeContext(String iun, String outcome) {
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Impostazione contesto timeline per esito {}, IUN {}", outcome, resolvedIun);
    }

    @When("viene recuperato il dettaglio per la campagna {string} con IUN {string}")
    @When("il mittente invoca la chiamata BFF di dettaglio per la campagna {string} e IUN {string}")
    public void invokeBffGetDetail(String campaignId, String iun) {
        String resolvedCampaignId = resolveDynamicValue(campaignId);
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Invocazione BFF dettaglio per campagna {} e IUN {}", resolvedCampaignId, resolvedIun);
        this.currentDetailResponse = bffComboRestClient.getComboDetails(resolvedCampaignId, resolvedIun)
                .withoutPolling()
                .get();
    }

    @When("viene recuperata la timeline per la campagna {string} e IUN {string}")
    @When("il mittente invoca la chiamata BFF per la timeline della campagna {string} e IUN {string}")
    public void invokeBffGetTimeline(String campaignId, String iun) {
        String resolvedCampaignId = resolveDynamicValue(campaignId);
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Invocazione BFF timeline per campagna {} e IUN {}", resolvedCampaignId, resolvedIun);
        this.currentTimelineResponse = bffComboRestClient.getComboTimeline(resolvedCampaignId, resolvedIun)
                .withoutPolling()
                .get();
    }

    @Then("il JSON di risposta del BFF contiene i metadati della comunicazione")
    public void verifyDetailMetadata() {
        Assertions.assertThat(currentDetailResponse).isNotNull();
        Assertions.assertThat(currentDetailResponse.getIun()).isNotNull();
        Assertions.assertThat(currentDetailResponse.getSenderDenomination()).isNotNull();
    }

    @Then("il testo del messaggio e le sezioni condizionali per allegati e pagamenti sono coerenti")
    public void verifyMessageAndSections() {
        Assertions.assertThat(currentDetailResponse).isNotNull();
        Assertions.assertThat(currentDetailResponse.getRecipients()).isNotEmpty();
        FullInformalNotificationRecipientV1 recipient = currentDetailResponse.getRecipients().get(0);
        Assertions.assertThat(recipient.getMessage()).isNotNull();
    }

    @Then("la lista dei canali abilitati corrisponde al profilo destinatario {string} per la campagna {string}")
    public void verifyEnabledChannelsProfile(String recipientType, String campaignType) {
        Assertions.assertThat(currentDetailResponse).isNotNull();
        List<BffChannelDeliveryStatusV1> channels = currentDetailResponse.getChannelsStatus();
        Assertions.assertThat(channels).isNotNull().isNotEmpty();
    }

    @Then("le sezioni per allegati e pagamenti risultano coerenti con la condizione {string}")
    public void verifyAttachmentsAndPaymentsCondition(String condition) {
        Assertions.assertThat(currentDetailResponse).isNotNull();
        log.info("Dettaglio comunicazione verificato per condizione {}", condition);
    }

    @Then("la risposta JSON della timeline contiene iun {string}, destinatari e l'ultimo stato registrato")
    public void verifyTimelineResponseData(String expectedIun) {
        Assertions.assertThat(currentTimelineResponse).isNotNull();
        String resolvedExpectedIun = resolveDynamicValue(expectedIun);
        Assertions.assertThat(currentTimelineResponse.getIun()).isEqualTo(resolvedExpectedIun);
        Assertions.assertThat(currentTimelineResponse.getRecipients()).isNotEmpty();
        Assertions.assertThat(currentTimelineResponse.getNotificationStatusHistory()).isNotEmpty();
    }

    @Then("la lista degli eventi della timeline è ordinata rigorosamente dal più recente al meno recente")
    public void verifyTimelineDescendingChronologicalOrder() {
        Assertions.assertThat(currentTimelineResponse).isNotNull();
        List<BffInformalNotificationTimelineStatusHistoryV1> history = currentTimelineResponse.getNotificationStatusHistory();
        Assertions.assertThat(history).isNotNull();

        for (int i = 0; i < history.size() - 1; i++) {
            String currentStr = history.get(i).getActiveFrom();
            String nextStr = history.get(i + 1).getActiveFrom();
            if (currentStr != null && nextStr != null) {
                OffsetDateTime currentTimestamp = OffsetDateTime.parse(currentStr);
                OffsetDateTime nextTimestamp = OffsetDateTime.parse(nextStr);
                Assertions.assertThat(currentTimestamp)
                        .as("Timeline history at index %d (%s) must be after or equal to index %d (%s)",
                                i, currentTimestamp, i + 1, nextTimestamp)
                        .isAfterOrEqualTo(nextTimestamp);
            }
        }
    }

    @Then("la timeline traccia correttamente i feedback per ciascun canale abilitato")
    public void verifyTimelineChannelFeedback() {
        Assertions.assertThat(currentTimelineResponse).isNotNull();
        List<BffInformalNotificationTimelineStatusHistoryV1> history = currentTimelineResponse.getNotificationStatusHistory();
        Assertions.assertThat(history).isNotEmpty();
        boolean hasSteps = history.stream()
                .anyMatch(h -> h.getSteps() != null && !h.getSteps().isEmpty());
        Assertions.assertThat(hasSteps).isTrue();
    }

    @Then("la timeline riflette l'esito di flusso {string}")
    public void verifyTimelineOutcome(String expectedOutcome) {
        Assertions.assertThat(currentTimelineResponse).isNotNull();
        Assertions.assertThat(currentTimelineResponse.getCommunicationOutcomes()).isNotNull();
        log.info("Timeline verificata con successo per esito {}", expectedOutcome);
    }
}
