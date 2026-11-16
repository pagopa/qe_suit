package it.pagopa.send.web.mittente.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.infrastructure.page.component.Footer;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.mittente.infrastructure.page.component.MittentiSidebar;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationSearchPage;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;

import java.time.Duration;
import java.util.List;
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

    MittentiSidebar sidebar();

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

    @XPath("//*[@data-testid=\"notificationsTable\"]")
    Table notificationsTable();

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//footer[contains(@class, 'MuiBox-root')]")
    Footer footer();

    @XPath("//*[@data-testid=\"sideMenuItem-API Key\"]")
    Button apiKeysMenuItem();

    // pagina "API Key" aperta dalla voce di menu
    @XPath("//main")
    APIKeyPage apiKeys();

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

    /**
     * Filtra le notifiche per IUN e attende che la tabella mostri solo quella notifica: senza filtro la notifica può
     * finire oltre la prima pagina se nel frattempo l'ente ne invia altre.
     */
    default void searchByIun(String iun) {
        searchNotification(Map.of("iun", iun));
        PollingUtils.pollUntil(
                // lo IUN è la quarta colonna
                () -> notificationsTable().tBody().rows().stream().map(row -> row.cells().get(3).read()).toList(),
                iuns -> iuns.equals(List.of(iun)),
                Duration.ofSeconds(30),
                Duration.ofSeconds(1),
                "La tabella filtrata non mostra solo la notifica " + iun);
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
