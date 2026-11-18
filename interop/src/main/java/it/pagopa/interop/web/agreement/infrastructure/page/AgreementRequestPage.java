package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.PageSize;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.interop.web.agreement.infrastructure.page.component.AgreementRequestTable;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.consumer-agreement-list}?limit=${limit}&offset=${offset}")
public interface AgreementRequestPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    AgreementRequestTable table();

    Pagination pagination();

    PageSize pageSize();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> pageTitle().read().equals("Richieste di fruizione inoltrate"),
                isLoaded -> isLoaded,
                "AgreementRequestPage not loaded"
        );
        table().assertLoaded();
    }
}
