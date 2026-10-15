package it.pagopa.interop.web.eservice.infrastructure.page.detail;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Label;

/**
 * 
Catalog list elements that must NOT appear in the detail page.
 */
public interface 
EServiceCatalogListElementsLocators
 {

    // ------------------------------- Elements of the catalog list (not expected)

    @XPath(".//h1[normalize-space()='Catalogo degli e-service']")
    Label catalogListTitle();

    @XPath(".//label[contains(normalize-space(), 'Cerca per nome')]")
    Label searchByNameField();

    @XPath(".//label[contains(normalize-space(), 'Cerca per erogatore')]")
    Label searchByProducerField();
}

