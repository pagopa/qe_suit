package it.pagopa.interop.web.eservice.infrastructure.page.component.provision;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.web.infrastructure.suit.component.TextField;
import org.openqa.selenium.Keys;

@Url("${interop.web.provision-catalog}")
public interface EServiceProvisionCatalogPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//input[@name='q']")
    TextField eserviceSearchNameField();

    @XPath(".//table//tbody/tr[1]//a[" +
            "normalize-space()='Visualizza' " +
            "and contains(@href, '/e-service/')" +
    "]")
    Clickable firstRowViewButton();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("I miei e-service");
    }

    default void searchEService(String value) {
        eserviceSearchNameField().writeAndAssert(value);
        eserviceSearchNameField().write(Keys.ENTER.name());
    }

    default void navigateToEService(EService eService) {
        searchEService(eService.getName());
        firstRowViewButton().click();
    }
}
