package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.RecipientNotificationsPage;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/notifiche}
 * Pagina "In arrivo per &lt;impresa&gt;" della persona giuridica, pagina iniziale del portale dopo il login.
 * Si apre dalla voce "In arrivo per &lt;impresa&gt;" del menu laterale "In arrivo".
 * È la stessa pagina "In arrivo" del cittadino, con il nome dell'impresa nel titolo: filtri, tabella e banner sono
 * definiti in {@link RecipientNotificationsPage}.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e i campi dei filtri; è usato anche dal login
 * degli step Cucumber. Testi, tabella, filtri e messaggi sono verificati da {@code WebNotificationPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.notifiche}")
public interface NotificationPage extends RecipientNotificationsPage {

    // il titolo contiene il nome dell'impresa, per esempio "In arrivo per Le Epistolae srl"
    String TITLE_PREFIX = "In arrivo per ";

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).startsWith(TITLE_PREFIX));
        Assertions.assertThat(communicationTypeSelect().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(iunSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(startDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(endDateSearchInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(filterButton().get(FindPolicy.PRESENT)).isPresent();
    }
}
