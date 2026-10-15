package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AppStatusPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Stato della piattaforma" del cittadino ({@code {baseUrl}/app-status}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>stato attuale dei servizi;</li>
 *     <li>storico dei disservizi e sua paginazione.</li>
 * </ul>
 * Stato e storico sono gli stessi per tutti gli utenti ma cambiano nel tempo: si verificano i testi fissi e il formato dei
 * dati (date, colonne, stati), non i loro valori. Nessuno scenario scarica le attestazioni.
 */
@ActiveProfiles({"test", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebAppStatusPFContractTest {

    // testi attesi

    private static final String TITLE = "Stato della piattaforma";
    private static final String SUBTITLE = "Verifica il funzionamento di SEND, visualizza lo storico dei disservizi e scarica le relative attestazioni opponibili a terzi. Ogni attestazione certifica un disservizio: potrebbe esserti utile nel caso in cui questo abbia coinvolto una notifica destinata a te.";
    private static final String HISTORY_TITLE = "Storico dei disservizi";
    private static final String LAST_CHECK_PATTERN = "Ultimo aggiornamento - .+, ore \\d{2}:\\d{2}";
    private static final List<String> HISTORY_HEADERS = List.of("Data di inizio", "Data di fine", "Servizio coinvolto", "Attestazioni opponibili a terzi", "Stato");
    // nella tabella data e ora sono su due righe: tra la virgola e "ore" può esserci un a capo
    private static final String DATE_TIME_PATTERN = "\\d{2}/\\d{2}/\\d{4},\\s+ore \\d{2}:\\d{2}";
    private static final String ROWS_PER_PAGE = "10";

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Testi della pagina: titolo, sottotitolo e titolo "Storico dei disservizi".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowAppStatusTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AppStatusPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "intestazione della pagina",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                }
                        ),

                        new WebScenario<>(
                                "titolo dello storico dei disservizi",
                                page -> {},
                                page -> page.downtimeHistoryTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(HISTORY_TITLE))
                        )
                ));
    }

    /**
     * Stato attuale dei servizi: presenza del messaggio, che cambia se è in corso un disservizio, e data dell'ultimo
     * aggiornamento nel formato "Ultimo aggiornamento - &lt;giorno&gt;, ore hh:mm".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowCurrentStatus() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AppStatusPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "messaggio dello stato attuale",
                                page -> {},
                                // il messaggio cambia se è in corso un disservizio: si verifica solo che ci sia
                                page -> page.statusBar().readAndAssert(h -> Assertions.assertThat(h).isNotBlank())
                        ),

                        new WebScenario<>(
                                "data e ora dell'ultimo aggiornamento",
                                page -> {},
                                page -> page.lastCheck().readAndAssert(h -> Assertions.assertThat(h).matches(LAST_CHECK_PATTERN))
                        )
                ));
    }

    /**
     * Storico dei disservizi: colonne della tabella e, per ogni riga, data di inizio nel formato {@code gg/mm/aaaa, ore
     * hh:mm}, servizio e stato; paginazione con 10 righe per pagina, prima pagina selezionata e "Indietro"
     * disabilitato. La data di fine e "Scarica l'attestazione" ci sono solo per i disservizi risolti e non vengono
     * verificati.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDowntimeHistory() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AppStatusPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "tabella dello storico con data di inizio, servizio e stato su ogni riga",
                                page -> {},
                                page -> {
                                    page.downtimeTableHeaders().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(HISTORY_HEADERS));
                                    page.downtimeStartDates().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(date -> Assertions.assertThat(date).matches(DATE_TIME_PATTERN)));
                                    page.downtimeServices().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(service -> Assertions.assertThat(service).isNotBlank()));
                                    page.downtimeStatuses().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(status -> Assertions.assertThat(status).isNotBlank()));
                                }
                        ),

                        new WebScenario<>(
                                "paginazione dello storico alla prima pagina",
                                page -> {},
                                page -> {
                                    page.rowsPerPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ROWS_PER_PAGE));
                                    page.firstPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("1"));
                                    Assertions.assertThat(page.previousPageButton().isDisabled()).isTrue();
                                    page.downtimeStartDates().readAllAndAssert(h -> Assertions.assertThat(h).hasSizeLessThanOrEqualTo(Integer.parseInt(ROWS_PER_PAGE)));
                                }
                        )
                ));
    }
}
