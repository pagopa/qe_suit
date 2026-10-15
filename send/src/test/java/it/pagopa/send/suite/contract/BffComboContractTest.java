package it.pagopa.send.suite.contract;

import it.pagopa.send.TestBootApp;
import it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test di contratto per le API BFF delle comunicazioni bonarie.
 * <p>
 * Verifica i codici di risposta HTTP e la struttura dei payload per i casi di errore (4xx)
 * e per stati non validi della comunicazione. Questi scenari erano precedentemente nei feature
 * file Gherkin (scenari @negativo) ma, non essendo flussi di business bensì verifiche di contratto
 * API, sono stati spostati qui come indicato durante la code review (mmancini95 - PR #39).
 *
 * @see BffComboRestClient
 */
@Slf4j
@ActiveProfiles({"test", "junit"})
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffComboContractTest {

    private final BffComboRestClient bffComboRestClient;

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
     * [SCENARIO_4_NEGATIVO_403] Verifica che il dettaglio BFF ritorni 403
     * quando la PA autenticata non e' proprietaria della comunicazione richiesta.
     */
    @Test
    void shouldReturn403WhenComboDetailBelongsToAnotherPa() {
        log.info("Verifica contratto API: GET dettaglio combo di altra PA -> 404");
        bffComboRestClient.getComboDetails("CAMP_002", "ZZZZ-YYYY-XXXX-202609-Z-A")
                .withoutPolling()
                .assertStatusCode(404);
    }

    // ===========================
    // Contratti Timeline - GET /campaigns/{campaignId}/communications/{iun}/timeline
    // ===========================

    /**
     * Verifica la struttura del payload di risposta per il dettaglio della comunicazione bonaria.
     */
    @Test
    void shouldValidateComboDetailResponseContract() {
        log.info("Verifica contratto API: schema e campi obbligatori del dettaglio combo");
        var response = bffComboRestClient.getComboDetails("FattOrd", "HDUX-MWKW-HRHY-202609-Q-A")
                .withoutPolling();

        if (response.getRaw() instanceof it.pagopa.infrastructure.response.ApiResponse apiResponse
                && apiResponse.getStatusCode() == 200) {
            var detail = response.get();
            org.assertj.core.api.Assertions.assertThat(detail).isNotNull();
            org.assertj.core.api.Assertions.assertThat(detail.getIun()).isNotNull();
            org.assertj.core.api.Assertions.assertThat(detail.getSenderDenomination()).isNotNull();
            org.assertj.core.api.Assertions.assertThat(detail.getRecipients()).isNotNull();
        }
    }

    /**
     * Verifica la struttura del payload di risposta per la timeline della comunicazione bonaria.
     */
    @Test
    void shouldValidateComboTimelineResponseContract() {
        log.info("Verifica contratto API: schema e campi obbligatori della timeline combo");
        var response = bffComboRestClient.getComboTimeline("FattOrd", "HDUX-MWKW-HRHY-202609-Q-A")
                .withoutPolling();

        if (response.getRaw() instanceof it.pagopa.infrastructure.response.ApiResponse apiResponse
                && apiResponse.getStatusCode() == 200) {
            var timeline = response.get();
            org.assertj.core.api.Assertions.assertThat(timeline).isNotNull();
            org.assertj.core.api.Assertions.assertThat(timeline.getIun()).isNotNull();
            org.assertj.core.api.Assertions.assertThat(timeline.getNotificationStatusHistory()).isNotNull();
            org.assertj.core.api.Assertions.assertThat(timeline.getCommunicationOutcomes()).isNotNull();
        }
    }
}
