package it.pagopa.send.suite.contract.destinatario_pf;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Contract test della pagina "In arrivo" del cittadino ({@code {baseUrl}/notifiche}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e dei filtri;</li>
 *     <li>tabella delle notifiche, paginazione e filtro per tipologia;</li>
 *     <li>banner per attivare il domicilio digitale, mostrato solo a chi non ce l'ha;</li>
 *     <li>messaggi di validazione dei filtri.</li>
 * </ul>
 * La tabella e il banner dipendono dall'utente: ogni scenario li verifica se presenti. Gli scenari non aprono le
 * notifiche, perché aprirle le segna come lette, e non chiudono il banner.
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
public class WebNotificationPFContractTest {

    // testi attesi

    private static final String TITLE = "In arrivo";
    private static final String COMMUNICATION_TYPE_LABEL = "Tipologia";
    private static final String IUN_LABEL = "Codice IUN";
    private static final String START_DATE_LABEL = "Dal";
    private static final String END_DATE_LABEL = "Al";
    private static final String FILTER = "Filtra";
    private static final List<String> COMMUNICATION_TYPES = List.of("Notifiche a valore legale", "Comunicazioni");

    // tabella
    private static final List<String> HEADERS = List.of("Data", "Mittente", "Oggetto", "Codice IUN", "");
    private static final String DATE_PATTERN = "Oggi|Ieri|\\d{2}/\\d{2}/\\d{4}";
    private static final String IUN_PATTERN = "[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-[A-Z0-9]";
    private static final String OPEN = "Apri";
    private static final String LEGAL_TAG = "Notifica a valore legale";
    private static final List<String> ROWS_PER_PAGE = List.of("10", "20", "50");

    // banner
    private static final String BANNER_TITLE = "Niente più documenti cartacei";
    private static final String BANNER_DESCRIPTION = "Attiva un domicilio digitale per ricevere le prossime notifiche solo in digitale e risparmiare.";
    private static final String BANNER_BUTTON = "Attiva domicilio digitale";

    // messaggi di validazione
    private static final String INVALID_IUN_MESSAGE = "Inserisci un codice IUN valido";
    // le date ammesse vanno da 10 anni fa a oggi
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // dati di prova

    private static final String INVALID_IUN = "abc";
    private static final String TOO_OLD_DATE = "01/01/2000";
    private static final String FUTURE_DATE = "01/01/2099";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e dei filtri

    @TestFactory
    Stream<DynamicTest> shouldShowNotificationListTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo, etichette dei filtri e filtra disabilitato",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.communicationTypeLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(COMMUNICATION_TYPE_LABEL));
                                    page.iunSearchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IUN_LABEL));
                                    page.startDateSearchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(START_DATE_LABEL));
                                    page.endDateSearchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(END_DATE_LABEL));
                                    // "Filtra" è disabilitato finché non si compila un filtro, quindi il testo si legge dall'elemento presente
                                    Assertions.assertThat(page.filterButton().get(FindPolicy.PRESENT).map(b -> b.getText())).hasValue(FILTER);
                                    Assertions.assertThat(page.filterButton().isDisabled()).isTrue();
                                }
                        ),

                        new WebScenario<>(
                                "tipologia propone notifiche a valore legale e comunicazioni",
                                page -> page.communicationTypeSelect().click(),
                                page -> page.communicationTypeOptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(COMMUNICATION_TYPES))
                        )
                ));
    }

    // tabella delle notifiche

    @TestFactory
    Stream<DynamicTest> shouldShowNotificationsTable() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se presenti, intestazioni e righe con data, mittente, oggetto, IUN e apri",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(OPEN)) {
                                        // Nessuna notifica ricevuta: la tabella non c'è, quindi il test termina
                                        return;
                                    }
                                    NotificationPFPage.NotificationsTable table = page.notificationsTable();
                                    table.headers().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(HEADERS));
                                    int rows = table.openButtons().readAll().size();
                                    table.dates().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(rows).allSatisfy(v -> Assertions.assertThat(v).matches(DATE_PATTERN)));
                                    table.senders().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(rows).allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                                    table.subjects().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(rows).allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                                    table.iuns().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(rows).allSatisfy(v -> Assertions.assertThat(v).matches(IUN_PATTERN)));
                                    table.openButtons().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(v -> Assertions.assertThat(v).isEqualTo(OPEN)));
                                }
                        ),

                        new WebScenario<>(
                                "se presenti, paginazione da 10 righe sulla prima pagina",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(OPEN)) {
                                        // Nessuna notifica ricevuta: la paginazione non c'è, quindi il test termina
                                        return;
                                    }
                                    NotificationPFPage.NotificationsTable table = page.notificationsTable();
                                    table.rowsPerPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ROWS_PER_PAGE.get(0)));
                                    Assertions.assertThat(table.openButtons().readAll()).hasSizeLessThanOrEqualTo(10);
                                    Assertions.assertThat(table.previousPageButton().isDisabled()).isTrue();
                                    table.firstPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("1"));
                                    Assertions.assertThat(table.nextPageButton().get(FindPolicy.PRESENT)).isPresent();
                                }
                        ),

                        new WebScenario<>(
                                "se presenti, righe per pagina propone 10, 20 e 50",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(OPEN)) {
                                        // Nessuna notifica ricevuta: la paginazione non c'è, quindi il test termina
                                        return;
                                    }
                                    page.notificationsTable().rowsPerPageButton().click();
                                    page.rowsPerPageOptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(ROWS_PER_PAGE));
                                }
                        ),

                        new WebScenario<>(
                                "se presenti, filtro notifiche a valore legale mostra solo righe con l'etichetta",
                                NotificationPFPage::filterLegalNotifications,
                                page -> {
                                    if (!page.content().read().contains(OPEN)) {
                                        // Nessuna notifica a valore legale: la tabella non c'è, quindi il test termina
                                        return;
                                    }
                                    List<String> subjects = filteredSubjects(page, s -> s.toLowerCase().contains(LEGAL_TAG.toLowerCase()));
                                    // l'etichetta è mostrata in maiuscolo dallo stile della pagina
                                    Assertions.assertThat(subjects).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).containsIgnoringCase(LEGAL_TAG));
                                }
                        ),

                        new WebScenario<>(
                                "se presenti, filtro comunicazioni mostra solo righe senza l'etichetta",
                                page -> {
                                    page.communicationTypeSelect().click();
                                    page.communicationsOption().click();
                                    page.filterButton().click();
                                },
                                page -> {
                                    if (!page.content().read().contains(OPEN)) {
                                        // Nessuna comunicazione: la tabella non c'è, quindi il test termina
                                        return;
                                    }
                                    List<String> subjects = filteredSubjects(page, s -> !s.toLowerCase().contains(LEGAL_TAG.toLowerCase()));
                                    Assertions.assertThat(subjects).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).doesNotContainIgnoringCase(LEGAL_TAG));
                                }
                        )
                ));
    }

    // banner per attivare il domicilio digitale ("Chiudi" non viene premuto)

    @TestFactory
    Stream<DynamicTest> shouldShowAddDomicileBanner() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se l'utente non ha un domicilio digitale, banner con titolo, descrizione e pulsante",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(BANNER_TITLE)) {
                                        // L'utente ha un domicilio digitale: il banner non c'è, quindi il test termina
                                        return;
                                    }
                                    NotificationPFPage.AddDomicileBanner banner = page.addDomicileBanner();
                                    banner.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BANNER_TITLE));
                                    banner.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BANNER_DESCRIPTION));
                                    banner.activateButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BANNER_BUTTON));
                                    Assertions.assertThat(banner.closeButton().get(FindPolicy.PRESENT)).isPresent();
                                }
                        ),

                        new WebScenario<>(
                                "se presente, attiva domicilio digitale apre il wizard di attivazione",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(BANNER_TITLE)) {
                                        // L'utente ha un domicilio digitale: il banner non c'è, quindi il test termina
                                        return;
                                    }
                                    page.addDomicileBanner().activateButton().click();
                                    page.digitalDomicileActivation().assertLoaded();
                                }
                        )
                ));
    }

    // messaggi di validazione dei filtri

    @TestFactory
    Stream<DynamicTest> shouldValidateNotificationFilters() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "IUN non valido",
                                page -> {},
                                page -> {
                                    page.iunSearchInput().write(INVALID_IUN);
                                    page.filterButton().click();
                                    page.iunErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(INVALID_IUN_MESSAGE));
                                }
                        ),

                        new WebScenario<>(
                                "data dal precedente a 10 anni fa",
                                page -> {},
                                page -> {
                                    // i campi data accettano il testo solo dopo essere stati svuotati
                                    page.startDateSearchInput().write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + TOO_OLD_DATE);
                                    page.startDateErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(dateRangeMessage()));
                                }
                        ),

                        new WebScenario<>(
                                "data al nel futuro",
                                page -> {},
                                page -> {
                                    page.endDateSearchInput().write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + FUTURE_DATE);
                                    page.endDateErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(dateRangeMessage()));
                                }
                        )
                ));
    }

    /**
     * Dopo "Filtra" la tabella si aggiorna con un breve ritardo: rilegge l'oggetto delle righe ogni secondo, per al massimo
     * 10 secondi, finché tutte rispettano il filtro, e restituisce l'ultima lettura su cui fare le verifiche.
     */
    private static List<String> filteredSubjects(NotificationPFPage page, Predicate<String> filtered) {
        List<String> subjects = page.notificationsTable().subjectCells().readAll();
        for (int i = 0; i < 10 && !(subjects.size() > 0 && subjects.stream().allMatch(filtered)); i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            subjects = page.notificationsTable().subjectCells().readAll();
        }
        return subjects;
    }

    // messaggio delle date fuori intervallo, calcolato sulla data di oggi
    private static String dateRangeMessage() {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Rome"));
        return "Inserisci una data compresa tra " + today.minusYears(10).format(DATE_FORMAT) + " e " + today.format(DATE_FORMAT);
    }
}
