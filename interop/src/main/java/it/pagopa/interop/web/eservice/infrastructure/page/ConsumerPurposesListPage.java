package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TextField;

@Url("${interop.web.base-url}/fruizione/finalita")
public interface ConsumerPurposesListPage extends Page {

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

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("Le mie finalità inoltrate");
    }
}