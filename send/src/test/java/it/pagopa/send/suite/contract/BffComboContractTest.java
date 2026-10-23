package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalRecipientSpecFactory;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffInformalNotificationTimelineStatusHistoryV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import it.pagopa.send.JunitActiveProfilesResolver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Test di contratto per le API BFF delle comunicazioni bonarie.
 * <p>
 * Verifica i codici di risposta HTTP e la struttura dei payload per i casi di errore (4xx)
 * e per stati non validi della comunicazione. Questi scenari erano precedentemente nei feature
 * file Gherkin (scenari @negativo) ma, non essendo flussi di business bensì verifiche di contratto
 * API, sono stati spostati qui come indicato durante la code review (mmancini95 - PR #39).
 *
 */
@Slf4j
@ActiveProfiles(resolver = JunitActiveProfilesResolver.class)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffComboContractTest {

    private final BffComboRestClient bffComboRestClient;
    private final SendJourney sendJourney;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;

    private InformalNotificationDomain createComboNotification(String subject) {
        InformalRecipientSpec lucrezia = informalRecipientSpecFactory.build(Recipient.LUCREZIA, Map.of());

        return sendJourney
                .withInformalSender(Tenant.GROSSINI)
                .prepareInformalNotification(Map.of(
                        "subject", subject,
                        "campaignId", "FattOrd"
                ))
                .withInformalRecipient(lucrezia)
                .sendInformalNotification(Tenant.GROSSINI);
    }

    // ===========================
    // Contratti Dettaglio - GET /campaigns/{campaignId}/communications/{iun}
    // ===========================

    /**
     * [SCENARIO_4_NEGATIVO_404] Verifica che il dettaglio BFF ritorni 404
     * quando lo IUN non corrisponde ad alcuna comunicazione bonaria esistente.
     */
    @Test
    void shouldReturn404WhenComboDetailNotFound() {
        log.info("Verifica contratto API: GET dettaglio combo con IUN inesistente -> 404");
        bffComboRestClient.getComboDetails("CAMP_001", "AAAA-BBBB-CCCC-202609-Z-A")
                .withoutPolling()
                .assertStatusCode(404);
    }

    /**
     * [SCENARIO_4_NEGATIVO_404_OBSCURITY] Verifica che il dettaglio BFF ritorni 404 (Obscurity)
     * quando la PA autenticata richiede una comunicazione non propria o non esistente.
     */
    @Test
    void shouldReturn404WhenComboDetailNotFoundOrUnauthorizedDueToObscurity() {
        log.info("Verifica contratto API: GET dettaglio combo di altra PA o inesistente -> 404 (Obscurity)");
        bffComboRestClient.getComboDetails("CAMP_002", "ZZZZ-YYYY-XXXX-202609-Z-A")
                .withoutPolling()
                .assertStatusCode(404);
    }

    /**
     * Verifica la struttura del payload di risposta per il dettaglio della comunicazione bonaria (schema DTO e campi obbligatori).
     * Migrato da dettaglio-combo-bff.feature.
     */
    @Test
    void shouldValidateComboDetailResponseContract() {
        log.info("Verifica contratto API: schema e campi obbligatori del dettaglio combo");
        InformalNotificationDomain created = createComboNotification("Test notifica bonaria per contratto BFF Dettaglio");

        var response = bffComboRestClient.getComboDetails(created.getCampaignId(), created.getIun())
                .withPolling(PollingStrategy.UNTIL_SUCCESS, Duration.of(90, ChronoUnit.SECONDS), Duration.of(3, ChronoUnit.SECONDS));

        response.assertStatusCode(200);
        var detail = response.get();
        Assertions.assertThat(detail).isNotNull();
        Assertions.assertThat(detail.getIun()).isNotNull();
        Assertions.assertThat(detail.getSenderDenomination()).isNotNull();
        Assertions.assertThat(detail.getRecipients()).isNotNull().isNotEmpty();
        Assertions.assertThat(detail.getRecipients().get(0).getMessage()).isNotNull();
        Assertions.assertThat(detail.getChannelsStatus()).isNotNull();
    }

    // ===========================
    // Contratti Timeline - GET /campaigns/{campaignId}/communications/{iun}/timeline
    // ===========================

    /**
     * Verifica la struttura del payload e l'ordinamento cronologico decrescente degli eventi della timeline combo.
     * Migrato da timeline-combo-bff.feature.
     */
    @Test
    void shouldValidateComboTimelineResponseContract() {
        log.info("Verifica contratto API: schema, campi obbligatori e ordinamento cronologico della timeline combo");
        InformalNotificationDomain created = createComboNotification("Test notifica bonaria per contratto BFF Timeline");

        var response = bffComboRestClient.getComboTimeline(created.getCampaignId(), created.getIun())
                .withPolling(PollingStrategy.UNTIL_SUCCESS, Duration.of(90, ChronoUnit.SECONDS), Duration.of(3, ChronoUnit.SECONDS));

        // Attesa che la timeline sia arricchita degli step dei canali
        org.awaitility.Awaitility.await()
                .atMost(Duration.ofSeconds(60))
                .pollInterval(Duration.ofSeconds(3))
                .until(() -> {
                    var currentTimeline = bffComboRestClient.getComboTimeline(created.getCampaignId(), created.getIun())
                            .withoutPolling()
                            .get();
                    return currentTimeline != null
                            && currentTimeline.getNotificationStatusHistory() != null
                            && currentTimeline.getNotificationStatusHistory().stream()
                                    .anyMatch(h -> h.getSteps() != null && !h.getSteps().isEmpty());
                });

        var finalResponse = bffComboRestClient.getComboTimeline(created.getCampaignId(), created.getIun())
                .withoutPolling();
        finalResponse.assertStatusCode(200);
        var timeline = finalResponse.get();

        Assertions.assertThat(timeline).isNotNull();
        Assertions.assertThat(timeline.getIun()).isNotNull();
        Assertions.assertThat(timeline.getRecipients()).isNotNull().isNotEmpty();
        Assertions.assertThat(timeline.getNotificationStatusHistory()).isNotNull().isNotEmpty();
        Assertions.assertThat(timeline.getCommunicationOutcomes()).isNotNull();

        // Verifica ordinamento cronologico rigoroso dal più recente al meno recente (descending)
        List<BffInformalNotificationTimelineStatusHistoryV1> history = timeline.getNotificationStatusHistory();
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

        // Verifica feedback e tracciamento step per i canali
        boolean hasSteps = history.stream()
                .anyMatch(h -> h.getSteps() != null && !h.getSteps().isEmpty());
        Assertions.assertThat(hasSteps).as("La timeline deve tracciare gli step dei canali").isTrue();
    }
}
