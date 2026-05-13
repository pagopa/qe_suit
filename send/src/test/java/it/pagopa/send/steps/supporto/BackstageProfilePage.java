package it.pagopa.send.steps.supporto;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.Property;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.steps.login.component.OneTrustBanner;
import it.pagopa.send.steps.login.page.AbstractComunePickerPage;

import java.util.Optional;

@Url("${url.notifiche.supporto.base}/auth/login/success#token=${token.supporto}")
public interface BackstageProfilePage extends AbstractComunePickerPage, Page {

    @XPath("//*[@id=\"search-institutions-autocomplete\"]")
    Writable<String> searchFor();

    @XPath("(//*[@role=\"option\"])[1]")
    Clickable breadcrumbs();

    @Property("selectors.supporto.portal")
    Clickable portals();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void selectComune(String comune) {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        searchFor().writeAndAssert(comune);
        breadcrumbs().click();
        portals().click();
    }
}
