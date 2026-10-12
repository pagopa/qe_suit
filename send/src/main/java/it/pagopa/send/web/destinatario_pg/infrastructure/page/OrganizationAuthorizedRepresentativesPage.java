package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/deleghe/delegati}
 * Scheda "Delegati dall'impresa" della pagina "Deleghe": le persone e le imprese a cui l'impresa ha delegato la lettura
 * delle sue notifiche. Si apre dalla scheda stessa nella pagina "Deleghe".
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo, la scheda selezionata, il titolo della sezione e
 * "Aggiungi una delega"; testi e contenuto, che dipende dai delegati dell'impresa, sono verificati da
 * {@code WebDelegationsPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.delegati}")
public interface OrganizationAuthorizedRepresentativesPage extends DelegationsPGPage {

    String TAB = "Delegati dall'impresa";

    @XPath("//*[@data-testid=\"addDeleghe\"]")
    Button addDelegationButton();

    // link "Aggiungi una delega" nel messaggio di sezione vuota
    @XPath("//*[@data-testid=\"link-add-delegate\"]")
    Button addDelegateLink();

    // link a pagina "Aggiungi una delega"
    @XPath("//main")
    NewDelegationPage newDelegation();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Deleghe"));
        selectedTab().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TAB));
        sectionTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TAB));
        addDelegationButton().assertLoaded();
    }
}
