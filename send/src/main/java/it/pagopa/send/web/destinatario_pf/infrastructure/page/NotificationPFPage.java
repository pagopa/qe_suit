package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationSearchPage;
import it.pagopa.send.web.notification_search.infrastructure.suit.RecipientNotificationsPage;
import org.assertj.core.api.Assertions;

import java.util.Map;

/**
 * {@code {baseUrl}/notifiche}
 * Pagina "In arrivo" del cittadino, pagina iniziale del portale dopo il login.
 * Si apre dalla voce "In arrivo" del menu laterale.
 * Filtri, tabella delle notifiche e banner per attivare il domicilio digitale, comuni alla pagina dell'impresa, sono
 * definiti in {@link RecipientNotificationsPage}; qui ci sono i pulsanti per aprire le notifiche e la ricerca usata
 * dagli step Cucumber.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e i campi dei filtri; è usato anche dagli step
 * Cucumber. Testi, tabella, filtri e messaggi sono verificati da {@code WebNotificationPFContractTest}.
 */
@Url("${url.notifiche.cittadino.notifiche}")
public interface NotificationPFPage extends NotificationSearchPage, RecipientNotificationsPage {

    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

    @XPath("//tbody/tr[1]/td[last()]//button")
    Clickable notificationDetailsButton();

    //Primo tasto apri disponibile sulla prima Notifica a valore legale
    @XPath("(//*[@data-testid=\"notificationsTable.body.row\"][.//span[normalize-space()=\"Notifica a valore legale\"]]//*[@data-testid=\"goToNotificationDetail\"])[1]")
    Button firstLegalNotificationDetailsButton();

    // "Apri" della prima notifica a valore legale già letta (senza il pallino di notifica nuova): aprirla non cambia lo stato di lettura
    @XPath("(//*[@data-testid=\"notificationsTable.body.row\"][not(.//*[@data-testid=\"new-notification-badge\"])][.//span[normalize-space()=\"Notifica a valore legale\"]]//*[@data-testid=\"goToNotificationDetail\"])[1]")
    Button firstReadLegalNotificationDetailsButton();

    // "Apri" della prima comunicazione già letta (senza il pallino di notifica nuova)
    @XPath("(//*[@data-testid=\"notificationsTable.body.row\"][not(.//*[@data-testid=\"new-notification-badge\"])][not(.//span[normalize-space()=\"Notifica a valore legale\"])]//*[@data-testid=\"goToNotificationDetail\"])[1]")
    Button firstReadCommunicationDetailsButton();

    //link a pagina di dettaglio notifica
    @XPath("//main")
    NotificationDetailsPFPage notificationDetails();

    // pagina del wizard di attivazione, aperta da "Attiva domicilio digitale" del banner
    @XPath("//main")
    DigitalDomicileActivationPFPage digitalDomicileActivation();

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
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("In arrivo"));
        Assertions.assertThat(communicationTypeSelect().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(iunSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(startDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(endDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(filterButton().get(FindPolicy.PRESENT)).isPresent();
    }
}
