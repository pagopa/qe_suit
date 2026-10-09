package it.pagopa.send.web.notification_search.infrastructure.suit;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.notification_search.infrastructure.suit.component.AddDomicileBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.component.NotificationsTable;

/**
 * Elementi comuni alla pagina "In arrivo" del cittadino ({@code NotificationPFPage}) e dell'impresa
 * ({@code NotificationPage} delle PG): oltre ai filtri di {@link NotificationFiltersPage}, il filtro "Tipologia", la
 * tabella delle notifiche e il banner per attivare il domicilio digitale. Le pagine concrete aggiungono URL, titolo
 * atteso e i propri elementi.
 * <p>
 * L'{@link Url} non è risolta: questa interfaccia non viene mai bindata direttamente, solo le sue implementazioni
 * concrete lo sono, ciascuna con la propria URL reale.
 */
@Url("about:blank")
public interface RecipientNotificationsPage extends NotificationFiltersPage {

    @XPath("//*[@id=\"communicationType-label\"]")
    Readable<String> communicationTypeLabel();

    @XPath("//*[@id=\"communicationType\"]")
    Button communicationTypeSelect();

    // opzioni della tendina "Tipologia", aperte fuori dal form in fondo alla pagina
    @XPath("//*[@role=\"listbox\"]//*[@role=\"option\"]")
    Readable<String> communicationTypeOptions();

    //Opzione Legal sul filtro tipologia (utile a filtrare solo le notifiche a valore legale)
    @XPath("//*[@role=\"listbox\"]//*[@data-value=\"LEGAL\"]")
    Button legalNotificationsOption();

    @XPath("//*[@role=\"listbox\"]//*[@data-value=\"INFORMAL\"]")
    Button communicationsOption();

    @XPath("//*[@data-testid=\"addDomicileBanner\"]")
    AddDomicileBanner addDomicileBanner();

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
