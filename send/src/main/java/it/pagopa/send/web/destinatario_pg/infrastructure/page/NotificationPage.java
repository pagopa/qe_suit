package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.infrastructure.page.component.Footer;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.RecipientNotificationsPage;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/notifiche}
 * Pagina "In arrivo per &lt;impresa&gt;" della persona giuridica, pagina iniziale del portale dopo il login.
 * Si apre dalla voce "In arrivo per &lt;impresa&gt;" del menu laterale "In arrivo".
 * È la stessa pagina "In arrivo" del cittadino, con il nome dell'impresa nel titolo: filtri, tabella e banner sono
 * definiti in {@link RecipientNotificationsPage}.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo, l'unico elemento presente per tutti gli utenti:
 * filtri, tabella e banner dipendono dalle notifiche ricevute, dai recapiti e dal ruolo. È usato anche dal login degli
 * step Cucumber. Il titolo completo è verificato da {@code WebNotificationPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.notifiche}#selfCareToken=${token}")
public interface NotificationPage extends RecipientNotificationsPage {

    // il titolo contiene il nome dell'impresa, per esempio "In arrivo per Le Epistolae srl"
    String TITLE_PREFIX = "In arrivo per ";

    @XPath("//footer[contains(@class, 'MuiBox-root')]")
    Footer footer();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).startsWith(TITLE_PREFIX));
    }
}
