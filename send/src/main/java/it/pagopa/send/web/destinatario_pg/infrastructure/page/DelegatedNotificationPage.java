package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.component.AddDomicileBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/notifiche-delegato}
 * Pagina "In arrivo dalle deleghe" della persona giuridica: le notifiche dei deleganti che hanno affidato una delega
 * all'impresa. Si apre dalla voce "In arrivo dalle deleghe" del menu laterale "In arrivo".
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo, l'unico elemento presente per tutti gli utenti:
 * filtri e tabella compaiono solo se ci sono notifiche delegate, il selettore del gruppo solo se l'impresa ha dei
 * gruppi. Il titolo e l'assenza del filtro "Tipologia" e del banner del domicilio digitale sono verificati da
 * {@code WebDelegatedNotificationPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.notifiche-delegato}")
public interface DelegatedNotificationPage extends Page {

    String TITLE = "In arrivo dalle deleghe";

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    // filtro "Tipologia" e banner per attivare il domicilio digitale: ci sono in "In arrivo per <impresa>", mai qui
    @XPath("//*[@id=\"communicationType\"]")
    Button communicationTypeSelect();

    @XPath("//*[@data-testid=\"addDomicileBanner\"]")
    AddDomicileBanner addDomicileBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
    }
}
