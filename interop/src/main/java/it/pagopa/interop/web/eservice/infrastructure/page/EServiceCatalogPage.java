package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceImportDrawer;

@Url("${interop.web.catalog}")
public interface EServiceCatalogPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//button[contains(., 'Importa')]")
    Button importButton();

    EServiceImportDrawer importDrawer();

    default void openImportDrawer() {
        importButton().click();
        importDrawer().assertLoaded();
    }

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("Catalogo degli e-service");
    }
}
