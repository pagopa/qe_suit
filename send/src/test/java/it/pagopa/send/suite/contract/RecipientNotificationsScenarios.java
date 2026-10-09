package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.web.notification_search.infrastructure.suit.RecipientNotificationsPage;
import it.pagopa.send.web.notification_search.infrastructure.suit.component.AddDomicileBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.component.NotificationsTable;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Scenari comuni ai contract test della pagina "In arrivo" del cittadino ({@code WebNotificationPFContractTest}) e
 * dell'impresa ({@code WebNotificationPGContractTest}), che hanno gli stessi filtri, la stessa tabella e lo stesso
 * banner. Ogni test sceglie l'utente e la pagina, passa il titolo atteso e aggiunge i propri scenari.
 * <p>
 * La tabella e il banner dipendono dall'utente: ogni scenario li verifica se presenti. Gli scenari non aprono le
 * notifiche, perché aprirle le segna come lette, e non chiudono il banner.
 */
public final class RecipientNotificationsScenarios {

    // testi attesi

    public static final String COMMUNICATION_TYPE_LABEL = "Tipologia";
    public static final String IUN_LABEL = "Codice IUN";
    public static final String START_DATE_LABEL = "Dal";
    public static final String END_DATE_LABEL = "Al";
    public static final String FILTER = "Filtra";
    public static final List<String> COMMUNICATION_TYPES = List.of("Notifiche a valore legale", "Comunicazioni");

    // tabella
    public static final List<String> HEADERS = List.of("Data", "Mittente", "Oggetto", "Codice IUN", "");
    public static final String DATE_PATTERN = "Oggi|Ieri|\\d{2}/\\d{2}/\\d{4}";
    public static final String IUN_PATTERN = "[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-[A-Z0-9]";
    public static final String OPEN = "Apri";
    public static final String LEGAL_TAG = "Notifica a valore legale";
    public static final List<String> ROWS_PER_PAGE = List.of("10", "20", "50");

    // banner
    public static final String BANNER_TITLE = "Niente più documenti cartacei";
    public static final String BANNER_DESCRIPTION = "Attiva un domicilio digitale per ricevere le prossime notifiche solo in digitale e risparmiare.";
    public static final String BANNER_BUTTON = "Attiva domicilio digitale";

    // messaggi di validazione
    public static final String INVALID_IUN_MESSAGE = "Inserisci un codice IUN valido";
    // le date ammesse vanno da 10 anni fa a oggi
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // dati di prova

    private static final String INVALID_IUN = "abc";
    private static final String TOO_OLD_DATE = "01/01/2000";
    private static final String FUTURE_DATE = "01/01/2099";

    private RecipientNotificationsScenarios() {
    }

    /**
     * Titolo, etichette dei filtri, "Filtra" disabilitato e opzioni di "Tipologia".
     */
    public static <P extends RecipientNotificationsPage> Stream<WebScenario<P>> texts(String title) {
        return Stream.of(
                new WebScenario<>(
                        "titolo, etichette dei filtri e filtra disabilitato",
                        page -> {},
                        page -> {
                            page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(title));
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
        );
    }

    /**
     * Righe della tabella, paginazione e filtro per tipologia.
     */
    public static <P extends RecipientNotificationsPage> Stream<WebScenario<P>> table() {
        return Stream.of(
                new WebScenario<>(
                        "se presenti, intestazioni e righe con data, mittente, oggetto, IUN e apri",
                        page -> {},
                        page -> {
                            if (!page.content().read().contains(OPEN)) {
                                // Nessuna notifica ricevuta: la tabella non c'è, quindi il test termina
                                return;
                            }
                            NotificationsTable table = page.notificationsTable();
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
                        "se presenti, righe per pagina vale 10 e propone 10, 20 e 50",
                        page -> {},
                        page -> {
                            if (!page.content().read().contains(OPEN)) {
                                // Nessuna notifica ricevuta: la paginazione non c'è, quindi il test termina
                                return;
                            }
                            NotificationsTable table = page.notificationsTable();
                            table.rowsPerPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ROWS_PER_PAGE.get(0)));
                            Assertions.assertThat(table.openButtons().readAll()).hasSizeLessThanOrEqualTo(10);
                            table.rowsPerPageButton().click();
                            page.rowsPerPageOptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(ROWS_PER_PAGE));
                        }
                ),

                new WebScenario<>(
                        "se ci sono più pagine, pulsanti di pagina sulla prima con indietro disabilitato",
                        page -> {},
                        page -> {
                            NotificationsTable table = page.notificationsTable();
                            if (!page.content().read().contains(OPEN) || table.nextPageButton().get(FindPolicy.PRESENT).isEmpty()) {
                                // Le notifiche stanno in una pagina: i pulsanti di pagina non ci sono, quindi il test termina
                                return;
                            }
                            Assertions.assertThat(table.previousPageButton().isDisabled()).isTrue();
                            table.firstPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("1"));
                        }
                ),

                new WebScenario<>(
                        "se presenti, filtro notifiche a valore legale mostra solo righe con l'etichetta",
                        RecipientNotificationsPage::filterLegalNotifications,
                        page -> {
                            List<String> subjects = filteredSubjects(page, s -> s.toLowerCase().contains(LEGAL_TAG.toLowerCase()));
                            if (subjects.isEmpty()) {
                                // Nessuna notifica a valore legale: la tabella non c'è, quindi il test termina
                                return;
                            }
                            // l'etichetta è mostrata in maiuscolo dallo stile della pagina
                            Assertions.assertThat(subjects).allSatisfy(v -> Assertions.assertThat(v).containsIgnoringCase(LEGAL_TAG));
                        }
                ),

                new WebScenario<>(
                        "se presenti, filtro comunicazioni mostra solo righe senza l'etichetta",
                        RecipientNotificationsPage::filterCommunications,
                        page -> {
                            List<String> subjects = filteredSubjects(page, s -> !s.toLowerCase().contains(LEGAL_TAG.toLowerCase()));
                            if (subjects.isEmpty()) {
                                // Nessuna comunicazione: la tabella non c'è, quindi il test termina
                                return;
                            }
                            Assertions.assertThat(subjects).allSatisfy(v -> Assertions.assertThat(v).doesNotContainIgnoringCase(LEGAL_TAG));
                        }
                )
        );
    }

    /**
     * Testi del banner per attivare il domicilio digitale, se l'utente non ce l'ha ("Chiudi" non viene premuto).
     */
    public static <P extends RecipientNotificationsPage> Stream<WebScenario<P>> addDomicileBanner() {
        return Stream.of(
                new WebScenario<>(
                        "se l'utente non ha un domicilio digitale, banner con titolo, descrizione e pulsante",
                        page -> {},
                        page -> {
                            if (!page.content().read().contains(BANNER_TITLE)) {
                                // L'utente ha un domicilio digitale: il banner non c'è, quindi il test termina
                                return;
                            }
                            AddDomicileBanner banner = page.addDomicileBanner();
                            banner.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BANNER_TITLE));
                            banner.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BANNER_DESCRIPTION));
                            banner.activateButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BANNER_BUTTON));
                            Assertions.assertThat(banner.closeButton().get(FindPolicy.PRESENT)).isPresent();
                        }
                )
        );
    }

    /**
     * Messaggi dei filtri con un IUN non valido e con date fuori dall'intervallo ammesso.
     */
    public static <P extends RecipientNotificationsPage> Stream<WebScenario<P>> filterValidations() {
        return Stream.of(
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
        );
    }

    /**
     * Dopo "Filtra" la tabella si aggiorna con un breve ritardo: ogni secondo, per al massimo 10 secondi, controlla il
     * contenuto della pagina. Se non c'è più nessun "Apri" la tabella è sparita e restituisce una lista vuota; altrimenti
     * rilegge l'oggetto delle righe finché tutte rispettano il filtro, e restituisce l'ultima lettura su cui fare le verifiche.
     * Il contenuto si legge da {@code main}, sempre presente, per non attendere righe che non ci sono più.
     */
    private static List<String> filteredSubjects(RecipientNotificationsPage page, Predicate<String> filtered) {
        List<String> subjects = List.of();
        for (int i = 0; i < 10; i++) {
            if (!page.content().read().contains(OPEN)) {
                return List.of();
            }
            subjects = page.notificationsTable().subjectCells().readAll();
            if (!subjects.isEmpty() && subjects.stream().allMatch(filtered)) {
                return subjects;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return subjects;
    }

    // messaggio delle date fuori intervallo, calcolato sulla data di oggi
    private static String dateRangeMessage() {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Rome"));
        return "Inserisci una data compresa tra " + today.minusYears(10).format(DATE_FORMAT) + " e " + today.format(DATE_FORMAT);
    }
}
