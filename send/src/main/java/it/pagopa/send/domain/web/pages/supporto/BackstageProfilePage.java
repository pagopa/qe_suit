package it.pagopa.send.domain.web.pages.supporto;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.domain.web.component.login.OneTrustBanner;
import it.pagopa.send.domain.web.commons.pages.login.AbstractComunePickerPage;

import java.util.Optional;

@Url("${url.notifiche.supporto.base}/auth/login/success#token=${token.supporto}")
public interface BackstageProfilePage extends AbstractComunePickerPage, Page {

    @XPath("//*[@id=\"search-institutions-autocomplete\"]")
    Writable<String> searchFor();

    @XPath("(//*[@role=\"option\"])[1]")
    Clickable breadcrumbs();

    @XPath("${selettore.supporto.area-riservata-selettore-env}")
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
