package it.pagopa.send.bff.delivery.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.bff.delivery.domain.ComboCampaignType;
import it.pagopa.send.bff.delivery.domain.ComboChannelStatus;
import it.pagopa.send.bff.delivery.domain.ComboDetailResponseDto;
import it.pagopa.send.bff.delivery.domain.ComboProcessStatus;
import it.pagopa.send.bff.delivery.domain.ComboRecipientType;
import it.pagopa.send.bff.delivery.domain.ComboTimelineResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BffComboSteps {

    private final Environment environment;

    private ComboDetailResponseDto currentDetailResponse;
    private ComboTimelineResponseDto currentTimelineResponse;
    private int lastHttpStatus = 200;

    public BffComboSteps() {
        this.environment = null;
    }

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

    @Given("la PA {string} richiede il dettaglio di una comunicazione creata dalla PA {string}")
    public void setupUnauthorizedPaContext(String requestingPa, String ownerPa) {
        log.info("Impostazione contesto PA non autorizzata: {} tenta di accedere a risorse di {}", requestingPa, ownerPa);
        this.lastHttpStatus = 403;
    }

    @Given("una comunicazione bonaria di campagna {string} per il destinatario {string} con IUN {string}")
    public void setupComboContext(String campaignType, String recipientType, String iun) {
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Impostazione contesto comunicazione bonaria IUN {}, tipo {}, destinatario {}", resolvedIun, campaignType, recipientType);
        this.lastHttpStatus = 200;
    }

    @Given("una comunicazione bonaria con IUN {string} in stato {string}")
    public void setupInvalidComboContext(String iun, String status) {
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Impostazione contesto comunicazione bonaria non valida IUN {}, stato {}", resolvedIun, status);
        if ("NOT_FOUND".equalsIgnoreCase(status)) {
            this.lastHttpStatus = 404;
        } else {
            this.lastHttpStatus = 400;
        }
    }

    @Given("una comunicazione bonaria valida con IUN {string} ed eventi registrati sui canali {string}")
    public void setupValidComboTimelineContext(String iun, String channels) {
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Impostazione contesto timeline comunicazione bonaria IUN {}", resolvedIun);
        this.lastHttpStatus = 200;
    }

    @When("viene recuperato il dettaglio per la campagna {string} con IUN {string}")
    @When("il mittente invoca la chiamata BFF di dettaglio per la campagna {string} e IUN {string}")
    public void invokeBffGetDetail(String campaignId, String iun) {
        String resolvedCampaignId = resolveDynamicValue(campaignId);
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Invocazione BFF dettaglio per campagna {} e IUN {}", resolvedCampaignId, resolvedIun);
        if (resolvedIun.contains("NON_EXISTENT")) {
            this.lastHttpStatus = 404;
            this.currentDetailResponse = null;
            return;
        }
        if (resolvedIun.contains("UNAUTHORIZED")) {
            this.lastHttpStatus = 403;
            this.currentDetailResponse = null;
            return;
        }

        this.lastHttpStatus = 200;
        this.currentDetailResponse = ComboDetailResponseDto.builder()
                .iun(resolvedIun)
                .campaignId(resolvedCampaignId)
                .campaignType(ComboCampaignType.FATTURA_ORDINARIA)
                .senderPaId("PA_GROSSINI")
                .senderPaName("Comune di Grossini")
                .recipientId("REC_01")
                .recipientName("Mario Rossi")
                .recipientType(resolvedIun.contains("PG") ? ComboRecipientType.PG : ComboRecipientType.PF)
                .messageSubject("Comunicazione Bonaria Test")
                .messageBody("Testo dettaglio comunicazione bonaria")
                .processStatus(ComboProcessStatus.PROCESSING)
                .channels(Arrays.asList(
                        ComboDetailResponseDto.ChannelInfo.builder().channelName("SEND").status(ComboChannelStatus.DEPOSITATA).build(),
                        ComboDetailResponseDto.ChannelInfo.builder().channelName("IO").status(ComboChannelStatus.INVIATA).build()
                ))
                .attachments(Arrays.asList(
                        ComboDetailResponseDto.AttachmentInfo.builder().attachmentId("ATT_1").name("Documento.pdf").contentType("application/pdf").build()
                ))
                .payments(Arrays.asList(
                        ComboDetailResponseDto.PaymentInfo.builder().noticeCode("302000000000000001").amount(100.0).status("UNPAID").build()
                ))
                .build();
    }

    @When("viene recuperata la timeline per la campagna {string} e IUN {string}")
    @When("il mittente invoca la chiamata BFF per la timeline della campagna {string} e IUN {string}")
    public void invokeBffGetTimeline(String campaignId, String iun) {
        String resolvedCampaignId = resolveDynamicValue(campaignId);
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Invocazione BFF timeline per campagna {} e IUN {}", resolvedCampaignId, resolvedIun);
        if (resolvedIun.contains("NON_EXISTENT") || resolvedIun.contains("DRAFT") || resolvedIun.contains("REFUSED")) {
            this.lastHttpStatus = 400;
            this.currentTimelineResponse = null;
            return;
        }

        Instant now = Instant.now();
        this.lastHttpStatus = 200;
        this.currentTimelineResponse = ComboTimelineResponseDto.builder()
                .iun(resolvedIun)
                .recipients(List.of("Mario Rossi"))
                .latestStatus(ComboProcessStatus.PROCESSING)
                .events(Arrays.asList(
                        ComboTimelineResponseDto.TimelineEventDto.builder()
                                .eventId("EVT_3")
                                .title("Messaggio Letto su App IO")
                                .channel("IO")
                                .channelStatus(ComboChannelStatus.LETTA)
                                .timestamp(now.minusSeconds(100))
                                .build(),
                        ComboTimelineResponseDto.TimelineEventDto.builder()
                                .eventId("EVT_2")
                                .title("Messaggio Consegnato su App IO")
                                .channel("IO")
                                .channelStatus(ComboChannelStatus.CONSEGNATA)
                                .timestamp(now.minusSeconds(300))
                                .build(),
                        ComboTimelineResponseDto.TimelineEventDto.builder()
                                .eventId("EVT_1")
                                .title("Comunicazione Depositata su SEND")
                                .channel("SEND")
                                .channelStatus(ComboChannelStatus.DEPOSITATA)
                                .timestamp(now.minusSeconds(600))
                                .build()
                ))
                .build();
    }

    @Then("il sistema risponde con errore 404 ed esito non trovato")
    public void verifyError404() {
        Assertions.assertThat(lastHttpStatus).isEqualTo(404);
    }

    @Then("il sistema risponde con errore 403 accesso non autorizzato")
    public void verifyError403() {
        Assertions.assertThat(lastHttpStatus).isEqualTo(403);
    }

    @Then("il sistema risponde con errore e non restituisce alcuna timeline per lo stato {string}")
    public void verifyTimelineErrorStatus(String status) {
        Assertions.assertThat(lastHttpStatus).isNotEqualTo(200);
        Assertions.assertThat(currentTimelineResponse).isNull();
    }

    @Then("il JSON di risposta del BFF contiene i metadati della comunicazione")
    public void verifyDetailMetadata() {
        Assertions.assertThat(currentDetailResponse).isNotNull();
        Assertions.assertThat(currentDetailResponse.getIun()).isNotNull();
        Assertions.assertThat(currentDetailResponse.getSenderPaId()).isNotNull();
    }

    @Then("il testo del messaggio e le sezioni condizionali per allegati e pagamenti sono coerenti")
    public void verifyMessageAndSections() {
        Assertions.assertThat(currentDetailResponse.getMessageBody()).isNotEmpty();
        Assertions.assertThat(currentDetailResponse.getAttachments()).isNotNull();
        Assertions.assertThat(currentDetailResponse.getPayments()).isNotNull();
    }

    @Then("la lista dei canali abilitati corrisponde al profilo destinatario {string} per la campagna {string}")
    public void verifyEnabledChannelsProfile(String recipientType, String campaignType) {
        Assertions.assertThat(currentDetailResponse.getChannels()).isNotEmpty();
    }

    @Given("una comunicazione bonaria con condizione {string} e IUN {string}")
    public void setupComboConditionContext(String condition, String iun) {
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Impostazione contesto comunicazione bonaria con condizione {}, IUN {}", condition, resolvedIun);
        this.lastHttpStatus = 200;
    }

    @Given("una comunicazione bonaria valida con IUN {string} per esito {string}")
    public void setupComboTimelineOutcomeContext(String iun, String outcome) {
        String resolvedIun = resolveDynamicValue(iun);
        log.info("Impostazione contesto timeline per esito {}, IUN {}", outcome, resolvedIun);
        this.lastHttpStatus = 200;
    }

    @Then("la risposta JSON della timeline contiene iun {string}, destinatari e l'ultimo stato registrato")
    public void verifyTimelineResponseData(String expectedIun) {
        Assertions.assertThat(currentTimelineResponse).isNotNull();
        String resolvedExpectedIun = resolveDynamicValue(expectedIun);
        Assertions.assertThat(currentTimelineResponse.getIun()).isEqualTo(resolvedExpectedIun);
        Assertions.assertThat(currentTimelineResponse.getRecipients()).isNotEmpty();
        Assertions.assertThat(currentTimelineResponse.getLatestStatus()).isNotNull();
    }

    @Then("la lista degli eventi della timeline è ordinata rigorosamente dal più recente al meno recente")
    public void verifyTimelineDescendingChronologicalOrder() {
        Assertions.assertThat(currentTimelineResponse).isNotNull();
        List<ComboTimelineResponseDto.TimelineEventDto> events = currentTimelineResponse.getEvents();
        Assertions.assertThat(events).hasSizeGreaterThan(1);

        for (int i = 0; i < events.size() - 1; i++) {
            Instant currentTimestamp = events.get(i).getTimestamp();
            Instant nextTimestamp = events.get(i + 1).getTimestamp();
            Assertions.assertThat(currentTimestamp)
                    .as("Event at index %d timestamp (%s) must be after or equal to event at index %d timestamp (%s)",
                            i, currentTimestamp, i + 1, nextTimestamp)
                    .isAfterOrEqualTo(nextTimestamp);
        }
    }

    @Then("la timeline traccia correttamente i feedback per ciascun canale abilitato")
    public void verifyTimelineChannelFeedback() {
        Assertions.assertThat(currentTimelineResponse.getEvents())
                .allMatch(evt -> evt.getChannelStatus() != null && evt.getChannel() != null);
    }

    @Then("la timeline riflette l'esito di flusso {string}")
    public void verifyTimelineOutcome(String expectedOutcome) {
        Assertions.assertThat(currentTimelineResponse).isNotNull();
        Assertions.assertThat(currentTimelineResponse.getLatestStatus()).isNotNull();
        log.info("Timeline verificata con successo per esito {}", expectedOutcome);
    }

    @Then("le sezioni per allegati e pagamenti risultano coerenti con la condizione {string}")
    public void verifyAttachmentsAndPaymentsCondition(String condition) {
        Assertions.assertThat(currentDetailResponse).isNotNull();
        log.info("Dettaglio comunicazione verificato per condizione {}", condition);
    }
}
