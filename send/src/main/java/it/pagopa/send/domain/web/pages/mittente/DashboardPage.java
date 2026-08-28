package it.pagopa.send.domain.web.pages.mittente;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.pagopa.send.domain.web.component.login.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationSearchPage;
import org.assertj.core.api.Assertions;

import java.util.Map;
import java.util.Optional;

@Url("${url.notifiche.mittente.dashboard}")
public interface DashboardPage extends NotificationSearchPage {

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/div/button")
    Clickable notificationDetails();

    @XPath("//*[@id=\"iunMatch\"]")
    Writable<String> iunSearchInput();

    @XPath("//*[@id=\"startDate\"]")
    Writable<String> startDateSearchInput();

    @XPath("//*[@id=\"endDate\"]")
    Writable<String> endDateSearchInput();

    /**
     * Filtro esclusivo del portale mittenti: assente lato PF.
     */
    @XPath("//*[@id=\"recipientId\"]")
    Writable<String> recipientIdSearchInput();

    @XPath("//*[@id=\"filter-button\"]")
    Clickable filterButton();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
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
