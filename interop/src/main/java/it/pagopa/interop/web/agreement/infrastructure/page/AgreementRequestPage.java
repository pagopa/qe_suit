package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.interop.web.agreement.infrastructure.page.component.AgreementRequestTable;

import java.util.Optional;

@Url("${interop.web.agreement-requests}?limit=${limit}&offset=${offset}")
public interface AgreementRequestPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    AgreementRequestTable table();

    Optional<Pagination> pagination();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("Richieste di fruizione inoltrate");
    }
}

