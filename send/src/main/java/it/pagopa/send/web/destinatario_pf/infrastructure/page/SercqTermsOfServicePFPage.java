package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/termini-di-servizio/sercq-send}
 * Pagina dei termini e condizioni d'uso del domicilio digitale SERCQ di SEND.
 * Si apre dal link "Termini del servizio" nel riepilogo del wizard di attivazione del domicilio digitale.
 * Il testo è caricato da un widget OneTrust: un indice con un link per ogni sezione e le sezioni del documento.
 * L'assertLoaded verifica che la pagina sia caricata, cioè che il titolo sia "Termini e condizioni d'uso" e che ci sia
 * almeno una sezione; titoli delle sezioni e indice sono verificati da {@code WebSercqTermsOfServicePFContractTest}.
 */
@Url("${url.notifiche.cittadino.termini-di-servizio-sercq-send}")
public interface SercqTermsOfServicePFPage extends Page {

    @XPath("(//section[starts-with(@id,\"otnotice-section-\")]//h2)[1]")
    Readable<String> title();

    @XPath("//section[starts-with(@id,\"otnotice-section-\")]")
    Readable<String> sections();

    @XPath("//section[starts-with(@id,\"otnotice-section-\")]/h2")
    Readable<String> sectionTitles();

    // indice per desktop: quello per mobile (otnotice-menu-mobile) è nascosto
    @XPath("//ul[@class=\"otnotice-menu\"]/li/a")
    Button indexLinks();

    // labels

    @XPath("(//section[starts-with(@id,\"otnotice-section-\")]//p)[1]")
    Readable<String> introduction();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Termini e condizioni d'uso"));
        sections().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty());
    }
}
