package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;

@Url("${interop.web.agreement}")
public interface AgreementListPage extends Page {

    @XPath(".//h1[normalize-space(.)='Richieste di fruizione ricevute']")
    Readable<String> pageTitle();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("Richieste di fruizione ricevute");
    }
}
