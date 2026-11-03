package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceProviderListFilters;
import it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceProviderListTable;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

import java.util.List;

@Url("${interop.web.base-url}/erogazione/e-service")
public interface EServiceProviderListPage extends Page {
    @XPath(".//main//h1")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath(".//main//button[contains(concat(' ', normalize-space(@class), ' '), ' MuiButton-root ')]")
    List<Button> primaryActions();

    @XPath(".//main")
    EServiceProviderListFilters filters();

    @XPath(".//main//table")
    EServiceProviderListTable table();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(pageTitle().read()).as("Provider e-service list page title available").isNotBlank();
        filters().nameInput().get(Gettable.FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Name search input not loaded"));
        filters().consumerInput().get(Gettable.FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Consumer search input not loaded"));
    }
}
