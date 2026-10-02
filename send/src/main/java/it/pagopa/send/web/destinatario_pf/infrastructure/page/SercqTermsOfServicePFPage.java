package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta i termini e condizioni d'uso del domicilio digitale SERCQ di SEND,
 * il cui testo è caricato da un widget OneTrust.
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
