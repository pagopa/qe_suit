package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;

@Url("sottoinsieme di ${url.selfcare.notifiche.base}")
public interface DashboardPartySelectionPage extends AbstractComunePickerPage {

    @XPath("//*[@id=\"search\"]")
    Writable<String> searchInput();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div/div/div/div[2]/div/div/div/div")
    Clickable comune();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[3]/div/button")
    Clickable accediButton();

    @Override
    default void selectComune(String comune) {
        searchInput().writeAndAssert(comune);
        this.comune().click();
        accediButton().click();
    }

}
