package it.pagopa.send.steps.supporto;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.pagopa.send.steps.login.page.AbstractComunePickerPage;

@Url("${url.notifiche.supporto.base}/auth/login/success#token=${token.supporto}")
public interface BackstageProfilePage extends AbstractComunePickerPage {

    @XPath("//*[@id=\"search-institutions-autocomplete\"]")
    Writable<String> searchFor();

    @XPath("(//*[@role=\"option\"])[1]")
    Clickable breadcrumbs();

    @XPath("//*[@id='root']//table//tr[4]/td[5]//button")
    Clickable portals();

    @Override
    default void selectComune(String comune) {
        searchFor().writeAndAssert(comune);
        breadcrumbs().click();
        portals().click();
    }
}
