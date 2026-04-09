package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;

@Url("sottoinsieme di ${url.selfcare.notifiche.base}")
public interface DashboardPartySelectionPage extends Page {

    @XPath("//*[@id=\"search\"]")
    Writable<String> searchInput();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div/div/div/div[2]/div/div/div/div")
    Clickable comune();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[3]/div/button")
    Clickable accediButton();

    default void selectComune(String comune) {
        searchInput().writeAndAssert(comune);
        this.comune().click();
        accediButton().click();
    }

}
