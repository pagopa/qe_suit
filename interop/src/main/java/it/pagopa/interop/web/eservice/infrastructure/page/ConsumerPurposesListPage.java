package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

@Url("${interop.web.base-url}/fruizione/finalita")
public interface ConsumerPurposesListPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//h1/following-sibling::p[1]")
    Label pageDescription();

    @XPath(".//button[normalize-space()='Crea nuovo']")
    Button createPurposeButton();

    @XPath(".//label[normalize-space()='Cerca per nome finalità']")
    Label purposeNameFilterLabel();

    @XPath(".//input[@name='q']")
    TextField purposeNameFilterInput();

    @XPath(".//label[normalize-space()='Cerca per e-service']")
    Label eServiceFilterLabel();

    @XPath(".//label[normalize-space()='Cerca per e-service']/following::input[@role='combobox'][1]")
    TextField eServiceFilterInput();

    @XPath(".//label[normalize-space()='Cerca per erogatore']")
    Label providerFilterLabel();

    @XPath(".//label[normalize-space()='Cerca per erogatore']/following::input[@role='combobox'][1]")
    TextField providerFilterInput();

    @XPath(".//label[normalize-space()='Stato della finalità']")
    Label statusFilterLabel();

    @XPath(".//label[normalize-space()='Stato della finalità']/following::input[@role='combobox'][1]")
    TextField statusFilterInput();

    @XPath(".//table")
    Table purposesTable();

    @XPath(".//nav[@aria-label='pagination navigation']")
    Pagination pagination();

    PageSize rowsPerPageSelector();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Fruizione", "Finalità inoltrate");

        Assertions.assertThat(purposesTable().get())
                .as("Purposes table should be present")
                .isPresent();

        pageTitle().readAndAssert("Le mie finalità inoltrate");
    }
}