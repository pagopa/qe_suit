package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/deleghe/a-carico}
 * Scheda "Deleghe a carico dell'impresa" della pagina "Deleghe": le deleghe affidate all'impresa da altri.
 * Si apre dalla voce "Deleghe" del menu laterale, che mostra questa scheda, o dalla scheda stessa.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo, la scheda selezionata e il titolo della sezione;
 * testi e contenuto, che dipende dalle deleghe dell'impresa, sono verificati da {@code WebDelegationsPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.deleghe-a-carico}")
public interface OrganizationDelegationsPage extends DelegationsPGPage {

    String TAB = "Deleghe a carico dell'impresa";

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Deleghe"));
        selectedTab().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TAB));
        sectionTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TAB));
    }
}
