package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta la sezione Deleghe del cittadino, con i delegati e le deleghe a proprio carico.
 * La pagina richiede un utente senza delegati e senza deleghe a proprio carico.
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
        delegatesEmptyState().readAndAssert(h -> Assertions.assertThat(h).startsWith("Non hai delegato nessuno alla visualizzazione delle tue notifiche."));
        addDelegateLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Aggiungi una delega"));
        delegatorsEmptyState().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Non hai deleghe a tuo carico."));

        // labels
        delegatesTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("I tuoi delegati"));
        delegatorsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Deleghe a tuo carico"));
    }
}
