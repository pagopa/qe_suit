package it.pagopa.send.domain.web.pages.mittente.login;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.infrastructure.suit.component.Button;

@Url("about:blank")
public interface PortalsEnvSelectionPage extends Page {

    @XPath("//div[@id='prod-pn-test']//button")
    Clickable testPortalBtn();

    @XPath("//div[@id='prod-pn']//button")
    Button uatPortalBtn();

    @XPath("//div[@id='prod-pn-hotfix']//button")
    Button hotfixPortalBtn();

    default void selectPortal(String tenant) {
//        searchFor().writeAndAssert(tenant);
//        tenantRow().click();
//        confirmBtn().click();
    }
}
