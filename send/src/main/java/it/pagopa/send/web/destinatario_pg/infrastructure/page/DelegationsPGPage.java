package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;

import java.util.Optional;

/**
 * Parti comuni alle due schede della pagina "Deleghe" dell'impresa: "Deleghe a carico dell'impresa"
 * ({@link OrganizationDelegationsPage}) e "Delegati dall'impresa" ({@link OrganizationAuthorizedRepresentativesPage}).
 * Le due schede hanno lo stesso titolo, lo stesso sottotitolo e le stesse schede; cambia la sezione mostrata.
 */
@Url("about:blank")
public interface DelegationsPGPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    // schede, nell'ordine in cui compaiono
    @XPath("//*[@role=\"tab\"]")
    Readable<String> tabs();

    @XPath("//*[@role=\"tab\"][@aria-selected=\"true\"]")
    Readable<String> selectedTab();

    // il testid "tab2" è la prima scheda
    @XPath("//*[@data-testid=\"tab2\"]")
    Button delegatorsTab();

    @XPath("//*[@data-testid=\"tab1\"]")
    Button delegatesTab();

    // titolo della sezione della scheda selezionata
    @XPath("//*[@role=\"tabpanel\"]//h2")
    Readable<String> sectionTitle();

    @XPath("//*[@role=\"tabpanel\"]//*[@data-testid=\"emptyState\"]")
    Readable<String> emptyState();

    // contenuto della scheda, per sapere se è vuota senza attendere la tabella
    @XPath("//*[@role=\"tabpanel\"]")
    Readable<String> content();

    // link alle schede
    @XPath("//main")
    OrganizationDelegationsPage delegationsOfTheCompany();

    @XPath("//main")
    OrganizationAuthorizedRepresentativesPage delegatesByCompany();
}
