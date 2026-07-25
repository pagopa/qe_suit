package it.pagopa.interop.new_arch.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;

@Url("${interop.web.catalog}")
public interface EServiceCatalogPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("Catalogo degli e-service");
    }
}
