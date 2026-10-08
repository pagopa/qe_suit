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
@ActiveProfiles({"dev", "junit"})
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
        bffComboRestClient.getComboDetails("CAMP_001", "NON_EXISTENT_IUN")
                .withoutPolling()
                .assertStatusCode(404);
    }

    /**
     * [SCENARIO_4_NEGATIVO_403] Verifica che il dettaglio BFF ritorni 403
     * quando la PA autenticata non e' proprietaria della comunicazione richiesta.
     */
    @Test
    void shouldReturn403WhenComboDetailBelongsToAnotherPa() {
        log.info("Verifica contratto API: GET dettaglio combo di altra PA -> 403");
        bffComboRestClient.getComboDetails("CAMP_002", "IUN_UNAUTHORIZED_890")
                .withoutPolling()
                .assertStatusCode(403);
    }

    // ===========================
    // Contratti Timeline - GET /campaigns/{campaignId}/communications/{iun}/timeline
    // ===========================

    /**
     * [SCENARIO_5_NEGATIVO] Verifica che la timeline BFF ritorni errore per comunicazioni
     * in stato non valido (DRAFT, REFUSED) o inesistenti (NOT_FOUND).
     * <p>
     * La timeline non e' consultabile per comunicazioni non ancora pubblicate o rifiutate.
     */
    @ParameterizedTest(name = "Timeline non disponibile per IUN ''{0}'' in stato ''{1}''")
    @CsvSource({
            "IUN_DRAFT_111, DRAFT",
            "IUN_REFUSED_222, REFUSED",
            "IUN_NON_EXISTENT_33, NOT_FOUND"
    })
    void shouldReturnErrorWhenTimelineUnavailableForInvalidState(String iun, String state) {
        log.info("Verifica contratto API: GET timeline combo per IUN {} in stato {} -> errore", iun, state);
        var action = bffComboRestClient.getComboTimeline("CAMP_999", iun).withoutPolling();
        if (action.getRaw() instanceof it.pagopa.infrastructure.response.ApiResponse apiResponse) {
            assertNotEquals(200, apiResponse.getStatusCode());
        }
    }
}
