package it.pagopa.send.web.mittente.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.infrastructure.page.component.Footer;
import it.pagopa.send.web.infrastructure.page.component.Table;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationSearchPage;
import it.pagopa.send.web.notification_search.infrastructure.suit.component.NotificationsTable;
import org.assertj.core.api.Assertions;

import java.util.Map;
import java.util.Optional;

/**
 * {@code {baseUrl}/dashboard}
 * Pagina "Notifiche" della PA: notifiche inviate dall'ente, con i filtri di ricerca e i pulsanti "Impostazioni lingua"
 * e "Invia una notifica". Filtri e tabella compaiono solo se l'ente ha inviato almeno una notifica.
 * Testi e stato iniziale sono verificati da {@code WebDashboardPaContractTest}.
 */
@Url("${url.notifiche.mittente.dashboard}")
public interface DashboardPage extends NotificationSearchPage {

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"settingsLangBtn\"]")
    Button languageSettingsButton();

    @XPath("//*[@id='notifications-table']/tbody/tr[1]//button[@data-testid='goToNotificationDetail']")
    Clickable notificationDetails();

    @XPath("//*[@data-testid=\"newNotificationBtn\"]")
    Button newNotificationButton();

    // pagina "Invia una nuova notifica" aperta con "Invia una nuova notifica"
    @XPath("//main")
    CreateNotificationPage createNotification();

    @XPath("//*[@id=\"iunMatch\"]")
    TextField iunSearchInput();

    @XPath("//*[@id=\"startDate\"]")
    TextField startDateSearchInput();

    @XPath("//*[@id=\"endDate\"]")
    TextField endDateSearchInput();

    Table notifications();

    /**
     * Filtro esclusivo del portale mittenti: assente lato PF.
     */
    @XPath("//*[@id=\"recipientId\"]")
    TextField recipientIdSearchInput();

    @XPath("//*[@id=\"filter-button\"]")
    Button filterButton();

    @XPath("//*[@data-testid=\"filter-form\"]//label")
    Readable<String> filterLabels();

    // valore mostrato dal filtro "Stato"
    @XPath("//*[@id=\"status\"]")
    Readable<String> statusFilter();

    NotificationsTable notificationsTable();

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//footer[contains(@class, 'MuiBox-root')]")
    Footer footer();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        header().readAndAssert((h) -> {
           Assertions.assertThat(h).isNotNull();
           Assertions.assertThat(h).isIn("Notifiche", "Notifications");
       });
    }

    @Override
    default void goToNotificationDetails() {
        notificationDetails().click();
    }

    /**
     * Chiavi supportate: {@code iun}, {@code startDate}, {@code endDate} (condivise con la ricerca
     * PF) più {@code recipientId} (solo mittente). {@code status} è un MUI Select, non un input di
     * testo: non ancora automatizzato qui, non essendo visibile nel markup l'elenco delle opzioni.
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
            case "recipientId" -> recipientIdSearchInput().cleanAndWrite(value);
            default -> throw new IllegalArgumentException("Parametro di ricerca notifica non supportato: " + key);
        }
    }
}
