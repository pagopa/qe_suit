package it.pagopa.send.steps.supporto;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;

@Url("${url.notifiche.supporto.base}/auth/login/success#token=${token.supporto}")
public interface BackstageProfilePage extends Page {

    @XPath("//*[@id=\"search-institutions-autocomplete\"]")
    Writable<String> searchFor();

    @XPath("//*[@id=\"search-institutions-autocomplete-option-0\"]")
    Clickable breadcrumbs();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/main/div/div[3]/div[3]/table/tbody/tr[2]/td[5]/button/span")
    Clickable portals();

    @Override
    default void assertLoaded() {
    }

    default void selectComune(String comune) {
        searchFor().writeAndAssert(comune);
        breadcrumbs().click();
        portals().click();
    }
}
