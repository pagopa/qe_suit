 package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceImportDrawer;
import org.assertj.core.api.Assertions;

/**
 * Pagina "I tuoi e-service" (lato erogazione, {@code /erogazione/e-service}), distinta dal
 * catalogo generale degli e-service ({@link EServiceCatalogPage}, {@code /catalogo-e-service}).
 * <p>
 * E' da questa pagina che si raggiunge il button "Importa" e il relativo drawer di
 * importazione e-service tramite file .zip.
 */
@Url("${interop.web.my-eservice}")
public interface MyEServiceCatalogPage extends Page {

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
        Assertions.assertThat(pageTitle().read()).isNotBlank();
    }
}

