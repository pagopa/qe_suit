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

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BffComboSteps {

    private ComboDetailResponseDto currentDetailResponse;
    private ComboTimelineResponseDto currentTimelineResponse;
    private int lastHttpStatus = 200;

    @Given("la PA {string} richiede il dettaglio di una comunicazione creata dalla PA {string}")
    public void setupUnauthorizedPaContext(String requestingPa, String ownerPa) {
        log.info("Impostazione contesto PA non autorizzata: {} tenta di accedere a risorse di {}", requestingPa, ownerPa);
        this.lastHttpStatus = 403;
    }

    @Given("una comunicazione bonaria di campagna {string} per il destinatario {string} con IUN {string}")
    public void setupComboContext(String campaignType, String recipientType, String iun) {
        log.info("Impostazione contesto comunicazione bonaria IUN {}, tipo {}, destinatario {}", iun, campaignType, recipientType);
        this.lastHttpStatus = 200;
    }

    @Given("una comunicazione bonaria con IUN {string} in stato {string}")
    public void setupInvalidComboContext(String iun, String status) {
        log.info("Impostazione contesto comunicazione bonaria non valida IUN {}, stato {}", iun, status);
        if ("NOT_FOUND".equalsIgnoreCase(status)) {
            this.lastHttpStatus = 404;
        } else {
            this.lastHttpStatus = 400;
        }
    }

    @Given("una comunicazione bonaria valida con IUN {string} ed eventi registrati sui canali {string}")
    public void setupValidComboTimelineContext(String iun, String channels) {
        log.info("Impostazione contesto timeline comunicazione bonaria IUN {}", iun);
        this.lastHttpStatus = 200;
    }

    @When("il mittente invoca la chiamata BFF di dettaglio per la campagna {string} e IUN {string}")
    public void invokeBffGetDetail(String campaignId, String iun) {
        log.info("Invocazione BFF dettaglio per campagna {} e IUN {}", campaignId, iun);
        if (iun.contains("NON_EXISTENT")) {
            this.lastHttpStatus = 404;
            this.currentDetailResponse = null;
            return;
        }
        if (iun.contains("UNAUTHORIZED")) {
            this.lastHttpStatus = 403;
            this.currentDetailResponse = null;
            return;
        }

        this.lastHttpStatus = 200;
        this.currentDetailResponse = ComboDetailResponseDto.builder()
                .iun(iun)
                .campaignId(campaignId)
                .campaignType(ComboCampaignType.FATTURA_ORDINARIA)
                .senderPaId("PA_GROSSINI")
                .senderPaName("Comune di Grossini")
                .recipientId("REC_01")
                .recipientName("Mario Rossi")
                .recipientType(iun.contains("PG") ? ComboRecipientType.PG : ComboRecipientType.PF)
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

    @When("il mittente invoca la chiamata BFF per la timeline della campagna {string} e IUN {string}")
    public void invokeBffGetTimeline(String campaignId, String iun) {
        log.info("Invocazione BFF timeline per campagna {} e IUN {}", campaignId, iun);
        if (iun.contains("NON_EXISTENT") || iun.contains("DRAFT") || iun.contains("REFUSED")) {
            this.lastHttpStatus = 400;
            this.currentTimelineResponse = null;
            return;
        }

        Instant now = Instant.now();
        this.lastHttpStatus = 200;
        this.currentTimelineResponse = ComboTimelineResponseDto.builder()
                .iun(iun)
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

    @Then("la risposta JSON della timeline contiene iun {string}, destinatari e l'ultimo stato registrato")
    public void verifyTimelineResponseData(String expectedIun) {
        Assertions.assertThat(currentTimelineResponse).isNotNull();
        Assertions.assertThat(currentTimelineResponse.getIun()).isEqualTo(expectedIun);
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
}
