package it.pagopa.send.web.login.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta la pagina di login di OneID per il mittente. Estende l'interfaccia AbstractOneIdPage e l'interfaccia Page.
 * È anche la pagina su cui si atterra dopo l'uscita dal portale ente mittente e dal BackOffice PT: l'assertLoaded
 * cerca il titolo "Accedi all'Area Riservata Enti" per testo, perché prima di arrivarci si passa da altre pagine.
 */
@Url("${url.notifiche.mittente.base}")
public interface OneIdPage extends AbstractOneIdPage, Page {

    String TITLE = "Accedi all'Area Riservata Enti";

    @XPath("//h3[normalize-space()=\"" + TITLE + "\"]")
    Readable<String> title();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
    }
}
