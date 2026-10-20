package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.interop.web.eservice.infrastructure.page.component.ConsumerPurposesFilters;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.base-url}/fruizione/finalita")
public interface ConsumerPurposesListPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//h1/following-sibling::p[1]")
    Label pageDescription();

    @XPath(".//button[normalize-space()='Crea nuovo']")
    Button createPurposeButton();

    @XPath(".//div[contains(@class,'MuiGrid-container')]")
    ConsumerPurposesFilters filters();

    @XPath(".//table")
    Table purposesTable();

    @XPath(".//nav[@aria-label='pagination navigation']")
    Pagination pagination();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                this::pageTitle,
                title -> pageTitle()
                        .read()
                        .equals("Le mie finalità inoltrate")
        );
    }
}