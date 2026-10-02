package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationSearchPage;
import org.assertj.core.api.Assertions;

import java.util.Map;
import java.util.Optional;

/**
 * Questa pagina rappresenta la pagina iniziale del portale delle notifiche per il cittadino
 * in cui vengono visualizzate tutte le notifiche ricevute.
 * La pagina contiene un elenco di notifiche con informazioni come il mittente, la data di ricezione e lo stato della notifica.
 * La tabella delle notifiche richiede un utente che abbia ricevuto almeno una notifica.
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

    @XPath("(//*[@data-testid=\"notificationsTable.body.row\"][.//span[normalize-space()=\"Notifica a valore legale\"]]//*[@data-testid=\"goToNotificationDetail\"])[1]")
    Button firstLegalNotificationDetailsButton();

    @XPath("//*[@id=\"communicationType-label\"]")
    Readable<String> communicationTypeLabel();

    @XPath("//*[@id=\"communicationType\"]")
    Readable<String> communicationTypeSelect();

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

    @XPath("//*[@data-testid=\"notificationsTable\"]")
    NotificationsTable notificationsTable();

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
            headers().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(5).startsWith("Data", "Mittente", "Oggetto", "Codice IUN"));
            dates().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
            senders().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
            subjects().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
            iuns().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).matches("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-[A-Z0-9]")));
            openButtons().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isEqualTo("Apri")));
            rowsPerPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("10"));
            Assertions.assertThat(previousPageButton().get(FindPolicy.PRESENT)).isPresent();
            firstPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("1"));
            Assertions.assertThat(nextPageButton().get(FindPolicy.PRESENT)).isPresent();
        }
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
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("In arrivo");
        });
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("In arrivo"));
        communicationTypeLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Tipologia"));
        communicationTypeSelect().readAndAssert(h -> Assertions.assertThat(h).isNotNull());
        iunSearchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Codice IUN"));
        Assertions.assertThat(iunSearchInput().get(FindPolicy.PRESENT)).isPresent();
        startDateSearchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Dal"));
        Assertions.assertThat(startDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        endDateSearchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Al"));
        Assertions.assertThat(endDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(filterButton().get(FindPolicy.PRESENT).map(WebPresentationElement::getText)).hasValue("Filtra");
        Assertions.assertThat(filterButton().isDisabled()).isTrue();
    }
}
