package it.pagopa.send.domain.web.pages.destinatario.pf;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationSearchPage;
import org.assertj.core.api.Assertions;

import java.util.Map;

/**
 * Questa pagina rappresenta la pagina iniziale del portale delle notifiche per il cittadino
 * in cui vengono visualizzate tutte le notifiche ricevute.
 * La pagina contiene un elenco di notifiche con informazioni come il mittente, la data di ricezione e lo stato della notifica.
 */
@Url("${url.notifiche.cittadino.notifiche}")
public interface NotificationPFPage extends NotificationSearchPage {

    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

    @XPath("//tbody/tr[1]/td[last()]//button")
    Clickable notificationDetailsButton();

    @XPath("//*[@id=\"iunMatch\"]")
    Writable<String> iunSearchInput();

    @XPath("//*[@id=\"startDate\"]")
    Writable<String> startDateSearchInput();

    @XPath("//*[@id=\"endDate\"]")
    Writable<String> endDateSearchInput();

    @XPath("//*[@id=\"filter-notifications-button\"]")
    Clickable filterButton();

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
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Your notifications", "Le tue notifiche");
        });
    }
}
