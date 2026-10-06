package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/deleghe}
 * Pagina "Deleghe" del cittadino.
 * Si apre dalla voce "Deleghe" del menu laterale.
 * Contiene la sezione "I tuoi delegati" (persone a cui l'utente ha delegato le proprie notifiche)
 * e la sezione "Deleghe a tuo carico" (persone che hanno delegato l'utente).
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo, il pulsante "Aggiungi una delega" e le due sezioni;
 * testi e contenuto delle sezioni, che dipende dalle deleghe dell'utente, sono verificati da {@code WebDelegationsPFContractTest}.
 */
@Url("${url.notifiche.cittadino.deleghe}")
public interface DelegationsPFPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"add-delegation\"]")
    Button addDelegationButton();

    @XPath("//*[@data-testid=\"delegates-wrapper\"]")
    Readable<String> delegatesSection();

    @XPath("//*[@data-testid=\"delegates-wrapper\"]//*[@data-testid=\"emptyState\"]")
    Readable<String> delegatesEmptyState();

    @XPath("//*[@data-testid=\"link-add-delegate\"]")
    Button addDelegateLink();

    @XPath("//*[@data-testid=\"delegators-wrapper\"]")
    Readable<String> delegatorsSection();

    @XPath("//*[@data-testid=\"delegators-wrapper\"]//*[@data-testid=\"emptyState\"]")
    Readable<String> delegatorsEmptyState();

    @XPath("//*[@data-testid=\"delegatesTable\"]")
    DelegationsTable delegatesTable();

    @XPath("//*[@data-testid=\"delegatorsTable\"]")
    DelegationsTable delegatorsTable();

    // link a pagina "Aggiungi una delega"
    @XPath("//main")
    NewDelegationPFPage newDelegation();

    // labels

    @XPath("//*[@data-testid=\"delegates-wrapper\"]//h2")
    Readable<String> delegatesTitle();

    @XPath("//*[@data-testid=\"delegators-wrapper\"]//h2")
    Readable<String> delegatorsTitle();

    /**
     * Tabella delle deleghe, uguale nelle due sezioni: compare solo se la sezione ha deleghe, al posto del messaggio di
     * sezione vuota. Ogni elemento legge la colonna di tutte le righe.
     */
    interface DelegationsTable extends Component {
        @XPath(".//th")
        Readable<String> headers();

        @XPath(".//tbody/tr/td[1]")
        Readable<String> names();

        @XPath(".//tbody/tr/td[2]")
        Readable<String> startDates();

        @XPath(".//tbody/tr/td[3]")
        Readable<String> endDates();

        @XPath(".//tbody/tr/td[4]")
        Readable<String> permissions();

        // stato della delega, oppure il pulsante "Accetta" per le deleghe a carico ancora da accettare
        @XPath(".//tbody/tr/td[5]")
        Readable<String> states();

        @XPath(".//tbody/tr//*[@data-testid=\"delegationMenuIcon\"]")
        Readable<String> menuButtons();
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Deleghe"));
        addDelegationButton().assertLoaded();
        delegatesSection().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        delegatorsSection().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
