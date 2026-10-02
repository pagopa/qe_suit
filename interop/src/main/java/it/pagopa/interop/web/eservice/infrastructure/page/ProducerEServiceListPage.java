package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;

@Url("${interop.web.erogazione-e-service}")
public interface ProducerEServiceListPage extends Page {

    @XPath(".//h1[normalize-space(.)='I miei e-service']")
    Readable<String> pageTitle();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("I miei e-service");
    }
}
