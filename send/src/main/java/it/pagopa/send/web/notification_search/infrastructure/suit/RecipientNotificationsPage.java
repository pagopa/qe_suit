package it.pagopa.send.web.notification_search.infrastructure.suit;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.component.AddDomicileBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.component.NotificationsTable;

import java.util.Optional;

/**
 * Elementi comuni alla pagina "In arrivo" del cittadino ({@code NotificationPFPage}) e dell'impresa
 * ({@code NotificationPage} delle PG): titolo, filtri con i loro messaggi, tabella delle notifiche e banner per
 * attivare il domicilio digitale. Le pagine concrete aggiungono URL, titolo atteso e i propri elementi.
 * <p>
 * L'{@link Url} non è risolta: questa interfaccia non viene mai bindata direttamente, solo le sue implementazioni
 * concrete lo sono, ciascuna con la propria URL reale.
 */
@Url("about:blank")
public interface RecipientNotificationsPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"communicationType-label\"]")
    Readable<String> communicationTypeLabel();

    @XPath("//*[@id=\"communicationType\"]")
    Button communicationTypeSelect();

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

    //Opzione Legal sul filtro tipologia (utile a filtrare solo le notifiche a valore legale)
    @XPath("//*[@role=\"listbox\"]//*[@data-value=\"LEGAL\"]")
    Button legalNotificationsOption();

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

    /**
     * Filtra la lista sulle sole notifiche a valore legale tramite il filtro "Tipologia".
     */
    default void filterLegalNotifications() {
        communicationTypeSelect().click();
        legalNotificationsOption().click();
        filterButton().click();
    }

    /**
     * Filtra la lista sulle sole comunicazioni tramite il filtro "Tipologia".
     */
    default void filterCommunications() {
        communicationTypeSelect().click();
        communicationsOption().click();
        filterButton().click();
    }
}
