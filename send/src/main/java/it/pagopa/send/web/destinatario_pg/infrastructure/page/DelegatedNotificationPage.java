package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationFiltersPage;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/notifiche-delegato}
 * Pagina "In arrivo dalle deleghe" della persona giuridica: le notifiche dei deleganti che hanno affidato una delega
 * all'impresa. Si apre dalla voce "In arrivo dalle deleghe" del menu laterale "In arrivo".
 * Ha i filtri per IUN e date di {@link NotificationFiltersPage}, senza "Tipologia", e una tabella con la colonna
 * "Destinatario" ({@link DelegatedNotificationsTable}), che compare solo se ci sono notifiche delegate.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e i campi dei filtri. Testi, tabella e messaggi
 * sono verificati da {@code WebDelegatedNotificationPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.notifiche-delegato}")
public interface DelegatedNotificationPage extends NotificationFiltersPage {

    String TITLE = "In arrivo dalle deleghe";

    @XPath("//*[@data-testid=\"notificationsTable\"]")
    DelegatedNotificationsTable notificationsTable();

    /**
     * Tabella delle notifiche delegate, con la paginazione. Rispetto a quella di "In arrivo" ha la colonna "Destinatario",
     * con il codice fiscale del delegante, prima del codice IUN.
     */
    interface DelegatedNotificationsTable extends Component {
        @XPath("//*[@data-testid=\"notificationsTable\"]//th")
        Readable<String> headers();

        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[1]//p")
        Readable<String> dates();

        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[2]")
        Readable<String> senders();

        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[3]//p")
        Readable<String> subjects();

        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[4]")
        Readable<String> recipients();

        @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[5]")
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
            headers().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty());
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
        Assertions.assertThat(iunSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(startDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(endDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(filterButton().get(FindPolicy.PRESENT)).isPresent();
    }
}
