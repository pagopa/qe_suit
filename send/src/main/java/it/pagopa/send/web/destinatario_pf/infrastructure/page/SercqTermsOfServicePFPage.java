package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/termini-di-servizio/sercq-send}
 * Pagina dei termini e condizioni d'uso del domicilio digitale SERCQ di SEND.
 * Si apre dal link "Termini del servizio" nel riepilogo del wizard di attivazione del domicilio digitale.
 * Il testo è caricato da un widget OneTrust: l'assertLoaded verifica solo il titolo e i titoli delle sezioni.
 */
@Url("${url.notifiche.cittadino.termini-di-servizio-sercq-send}")
public interface SercqTermsOfServicePFPage extends Page {

    @XPath("(//section[starts-with(@id,\"otnotice-section-\")]//h2)[1]")
    Readable<String> title();

    @XPath("//section[starts-with(@id,\"otnotice-section-\")]/h2")
    Readable<String> sectionTitles();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Termini e condizioni d'uso"));
        sectionTitles().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().startsWith("1. Descrizione del SERCQ"));
    }
}
