package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/deleghe}
 * Pagina "Deleghe" del cittadino.
 * Si apre dalla voce "Deleghe" del menu laterale.
 * Contiene la sezione "I tuoi delegati" (persone a cui l'utente ha delegato le proprie notifiche)
 * e la sezione "Deleghe a tuo carico" (persone che hanno delegato l'utente).
 * L'assertLoaded verifica solo gli elementi presenti per qualunque utente; gli stati vuoti delle due sezioni
 * compaiono solo se l'utente non ha deleghe e non vengono verificati.
 */
@Url("${url.notifiche.cittadino.deleghe}")
public interface DelegationsPFPage extends Page {

    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"add-delegation\"]")
    Button addDelegationButton();

    @XPath("//*[@data-testid=\"delegates-wrapper\"]//*[@data-testid=\"emptyState\"]")
    Readable<String> delegatesEmptyState();

    @XPath("//*[@data-testid=\"link-add-delegate\"]")
    Button addDelegateLink();

    @XPath("//*[@data-testid=\"delegators-wrapper\"]//*[@data-testid=\"emptyState\"]")
    Readable<String> delegatorsEmptyState();

    // labels

    @XPath("//*[@data-testid=\"delegates-wrapper\"]//h2")
    Readable<String> delegatesTitle();

    @XPath("//*[@data-testid=\"delegators-wrapper\"]//h2")
    Readable<String> delegatorsTitle();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert(h -> Assertions.assertThat(h).isIn("Delegates", "Deleghe"));
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Deleghe"));
        subtitle().readAndAssert(h -> Assertions.assertThat(h).startsWith("Qui puoi gestire i tuoi delegati e le deleghe a tuo carico."));
        addDelegationButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Aggiungi una delega"));

        // labels
        delegatesTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("I tuoi delegati"));
        delegatorsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Deleghe a tuo carico"));
    }
}
