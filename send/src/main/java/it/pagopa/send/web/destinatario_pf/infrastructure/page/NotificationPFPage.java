package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationSearchPage;
import org.assertj.core.api.Assertions;

import java.util.Map;
import java.util.Optional;

/**
 * {@code {baseUrl}/notifiche}
 * Pagina "In arrivo" del cittadino, pagina iniziale del portale dopo il login.
 * Si apre dalla voce "In arrivo" del menu laterale.
 * Contiene i filtri di ricerca e la tabella delle notifiche ricevute; a chi non ha un domicilio digitale mostra anche il
 * banner {@link AddDomicileBanner}. Il componente {@link NotificationsTable} mappa la tabella, che compare solo se
 * l'utente ha ricevuto almeno una notifica.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e i campi dei filtri; è usato anche dagli step
 * Cucumber. Testi, tabella, filtri e messaggi sono verificati da {@code WebNotificationPFContractTest}.
 */
@Url("${url.notifiche.cittadino.notifiche}")
public interface NotificationPFPage extends NotificationSearchPage {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//tbody/tr[1]/td[last()]//button")
    Clickable notificationDetailsButton();

    //Primo tasto apri disponibile sulla prima Notifica a valore legale
    @XPath("(//*[@data-testid=\"notificationsTable.body.row\"][.//span[normalize-space()=\"Notifica a valore legale\"]]//*[@data-testid=\"goToNotificationDetail\"])[1]")
    Button firstLegalNotificationDetailsButton();

    @XPath("//*[@id=\"communicationType-label\"]")
    Readable<String> communicationTypeLabel();

    @XPath("//*[@id=\"communicationType\"]")
    Button communicationTypeSelect();

    //Opzione Legal sul filtro tipologia (utile a filtrare solo le notifiche a valore legale)
    @XPath("//*[@role=\"listbox\"]//*[@data-value=\"LEGAL\"]")
    Button legalNotificationsOption();

    @XPath("//*[@id=\"iunMatch-label\"]")
    Readable<String> iunSearchLabel();

    @XPath("//*[@id=\"iunMatch\"]")
    TextField iunSearchInput();

    @XPath("//*[@id=\"startDate-label\"]")
    Readable<String> startDateSearchLabel();

    @XPath("//*[@id=\"startDate\"]")
    TextField startDateSearchInput();

    @XPath("//*[@id=\"endDate-label\"]")
    Readable<String> endDateSearchLabel();

    @XPath("//*[@id=\"endDate\"]")
    TextField endDateSearchInput();

    @XPath("//*[@id=\"filter-notifications-button\"]")
    Button filterButton();

    // opzioni della tendina "Tipologia", aperte fuori dal form in fondo alla pagina
    @XPath("//*[@role=\"listbox\"]//*[@role=\"option\"]")
    Readable<String> communicationTypeOptions();

    @XPath("//*[@role=\"listbox\"]//*[@data-value=\"INFORMAL\"]")
    Button communicationsOption();

    @XPath("//*[@id=\"iunMatch-helper-text\"]")
    Readable<String> iunErrorMessage();

    @XPath("//*[@id=\"startDate-helper-text\"]")
    Readable<String> startDateErrorMessage();

    @XPath("//*[@id=\"endDate-helper-text\"]")
    Readable<String> endDateErrorMessage();

    // contenuto della pagina, per sapere se ci sono il banner e la tabella senza attenderli
    @XPath("//main")
    Readable<String> content();

    @XPath("//*[@data-testid=\"addDomicileBanner\"]")
    AddDomicileBanner addDomicileBanner();

    // opzioni del menu "Righe per pagina"
    @XPath("//*[@role=\"menu\"]//*[@role=\"menuitem\"]")
    Readable<String> rowsPerPageOptions();

    @XPath("//*[@data-testid=\"notificationsTable\"]")
    NotificationsTable notificationsTable();

    //link a pagina di dettaglio notifica
    @XPath("//main")
    NotificationDetailsPFPage notificationDetails();

    interface NotificationsTable extends Component {
        @XPath("//*[@data-testid=\"notificationsTable\"]//th")
        Readable<String> headers();

        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[1]//p")
        Readable<String> dates();

        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[2]")
        Readable<String> senders();

        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[3]//p")
        Readable<String> subjects();

        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[4]")
        Readable<String> iuns();

        // oggetto di ogni riga con l'eventuale etichetta "Notifica a valore legale"
        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[3]")
        Readable<String> subjectCells();

        @XPath("//*[@data-testid=\"goToNotificationDetail\"]")
        Readable<String> openButtons();

        @XPath("//*[@id=\"rows-per-page\"]")
        Button rowsPerPageButton();

        @XPath("//*[@id=\"previous\"]")
        Button previousPageButton();

        @XPath("//*[@id=\"page1\"]")
        Button firstPageButton();

        @XPath("//*[@id=\"next\"]")
        Button nextPageButton();

        @Override
        default void assertLoaded() {
            headers().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty());
        }
    }

    /**
     * Banner "Niente più documenti cartacei", mostrato solo a chi non ha un domicilio digitale. "Chiudi" lo nasconde.
     */
    interface AddDomicileBanner extends Component {
        @XPath("//*[@data-testid=\"addDomicileBanner\"]//h6")
        Readable<String> title();

        @XPath("//*[@data-testid=\"addDomicileBanner\"]//p")
        Readable<String> description();

        @XPath("//*[@data-testid=\"addDomicileBanner\"]//button[normalize-space()=\"Attiva domicilio digitale\"]")
        Button activateButton();

        @XPath("//*[@data-testid=\"addDomicileBanner\"]//button[@aria-label=\"Chiudi\"]")
        Button closeButton();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    // pagina del wizard di attivazione, aperta da "Attiva domicilio digitale" del banner
    @XPath("//main")
    DigitalDomicileActivationPFPage digitalDomicileActivation();

    /**
     * Filtra la lista sulle sole notifiche a valore legale tramite il filtro "Tipologia".
     */
    default void filterLegalNotifications() {
        communicationTypeSelect().click();
        legalNotificationsOption().click();
        filterButton().click();
    }

    @Override
    default void goToNotificationDetails() {
        notificationDetailsButton().click();
    }

    /**
     * Compila i filtri di ricerca indicati (chiavi supportate: {@code iun}, {@code startDate},
     * {@code endDate}) e lancia la ricerca. Non seleziona alcun risultato: per aprire il primo
     * risultato trovato si riusa {@link #goToNotificationDetails()}, che punta già alla prima riga
     * della tabella indipendentemente dal fatto che sia filtrata o meno.
     */
    @Override
    default void searchNotification(Map<String, String> searchParams) {
        searchParams.forEach(this::applySearchParam);
        filterButton().click();
    }

    private void applySearchParam(String key, String value) {
        switch (key) {
            case "iun" -> iunSearchInput().cleanAndWrite(value);
            case "startDate" -> startDateSearchInput().cleanAndWrite(value);
            case "endDate" -> endDateSearchInput().cleanAndWrite(value);
            default -> throw new IllegalArgumentException("Parametro di ricerca notifica non supportato: " + key);
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("In arrivo"));
        Assertions.assertThat(communicationTypeSelect().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(iunSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(startDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(endDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(filterButton().get(FindPolicy.PRESENT)).isPresent();
    }
}
