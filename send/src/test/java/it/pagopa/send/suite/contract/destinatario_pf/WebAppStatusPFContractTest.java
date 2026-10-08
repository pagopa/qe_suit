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
 * Stato e storico cambiano nel tempo: si verificano i testi fissi e il formato dei dati (date, colonne, stati), non i loro
 * valori. Lo storico è verificato se presente. Nessuno scenario scarica le attestazioni.
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
    private static final String ALL_SERVICES_WORKING = "Tutti i servizi di SEND sono operativi.";
    private static final String LAST_CHECK_PATTERN = "Ultimo aggiornamento - .+, ore \\d{2}:\\d{2}";
    private static final List<String> HISTORY_HEADERS = List.of("Data di inizio", "Data di fine", "Servizio coinvolto", "Attestazioni opponibili a terzi", "Stato");
    // nella tabella data e ora sono su due righe: tra la virgola e "ore" può esserci un a capo
    private static final String DATE_TIME_PATTERN = "\\d{2}/\\d{2}/\\d{4},\\s+ore \\d{2}:\\d{2}";
    private static final String DOWNLOAD_LEGAL_FACT = "Scarica l'attestazione";
    private static final String RESOLVED = "Risolto";
    private static final String ROWS_PER_PAGE = "10";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina

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

    // stato attuale dei servizi

    @TestFactory
    Stream<DynamicTest> shouldShowCurrentStatus() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AppStatusPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "stato attuale: se tutti i servizi sono operativi, messaggio e icona verde",
                                page -> {},
                                page -> {
                                    String status = page.statusBar().read();
                                    Assertions.assertThat(status).isNotBlank();
                                    if (!status.equals(ALL_SERVICES_WORKING)) {
                                        // È in corso un disservizio: il messaggio dipende dal disservizio, quindi si verifica solo che ci sia
                                        return;
                                    }
                                    // l'icona non ha testo: se c'è la lettura restituisce un testo vuoto, se manca null
                                    Assertions.assertThat(page.allServicesWorkingIcon().read()).as("icona verde dello stato").isNotNull();
                                }
                        ),

                        new WebScenario<>(
                                "data e ora dell'ultimo aggiornamento",
                                page -> {},
                                page -> page.lastCheck().readAndAssert(h -> Assertions.assertThat(h).matches(LAST_CHECK_PATTERN))
                        )
                ));
    }

    // storico dei disservizi, se presente

    @TestFactory
    Stream<DynamicTest> shouldShowDowntimeHistory() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AppStatusPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se presente, tabella dello storico con date, servizio, attestazione e stato su ogni riga",
                                page -> {},
                                page -> {
                                    if (!hasHistory(page)) {
                                        // Nessun disservizio registrato: la tabella non c'è, quindi il test termina senza verificarla
                                        return;
                                    }
                                    page.downtimeTableHeaders().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(HISTORY_HEADERS));
                                    page.downtimeStartDates().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(date -> Assertions.assertThat(date).matches(DATE_TIME_PATTERN)));
                                    page.downtimeServices().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(service -> Assertions.assertThat(service).isNotBlank()));
                                    page.downtimeStatuses().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(status -> Assertions.assertThat(status).isNotBlank()));
                                    // un disservizio risolto ha la data di fine e l'attestazione da scaricare
                                    List<String> statuses = page.downtimeStatuses().readAll();
                                    List<String> endDates = page.downtimeEndDates().readAll();
                                    for (int i = 0; i < statuses.size(); i++) {
                                        if (RESOLVED.equals(statuses.get(i))) {
                                            Assertions.assertThat(endDates.get(i)).as("data di fine della riga %d", i + 1).matches(DATE_TIME_PATTERN);
                                        }
                                    }
                                    long resolved = statuses.stream().filter(RESOLVED::equals).count();
                                    page.downloadLegalFactButtons().readAllAndAssert(h -> Assertions.assertThat(h)
                                            .hasSizeGreaterThanOrEqualTo((int) resolved)
                                            .allSatisfy(label -> Assertions.assertThat(label).isEqualTo(DOWNLOAD_LEGAL_FACT)));
                                }
                        ),

                        new WebScenario<>(
                                "se presente, paginazione dello storico alla prima pagina",
                                page -> {},
                                page -> {
                                    if (!hasHistory(page)) {
                                        // Nessun disservizio registrato: la paginazione non c'è, quindi il test termina senza verificarla
                                        return;
                                    }
                                    page.rowsPerPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ROWS_PER_PAGE));
                                    page.firstPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("1"));
                                    Assertions.assertThat(page.previousPageButton().isDisabled()).isTrue();
                                    page.downtimeStartDates().readAllAndAssert(h -> Assertions.assertThat(h).hasSizeLessThanOrEqualTo(Integer.parseInt(ROWS_PER_PAGE)));
                                }
                        )
                ));
    }

    // lo storico c'è se la pagina mostra le intestazioni della tabella; si legge il contenuto per non attendere la tabella
    private boolean hasHistory(AppStatusPFPage page) {
        return page.content().read().contains(HISTORY_HEADERS.get(0));
    }
}
