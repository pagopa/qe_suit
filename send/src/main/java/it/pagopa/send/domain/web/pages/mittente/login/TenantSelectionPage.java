package it.pagopa.send.domain.web.pages.mittente.login;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.pagopa.send.domain.web.commons.pages.login.AbstractComunePickerPage;
import it.pagopa.send.domain.web.component.login.OneTrustBanner;
import it.pagopa.send.web.infrastructure.suit.component.Button;

import java.util.Optional;

public interface TenantSelectionPage extends AbstractComunePickerPage {

    @XPath("//*[@id=\"search\"]")
    Writable<String> searchFor();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div/div/div/div[2]/div/div/div/div")
    Clickable tenantRow();

    @XPath(".//button[normalize-space()='Login']")
    Button confirmBtn();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void selectComune(String tenant) {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        searchFor().writeAndAssert(tenant);
        tenantRow().click();
        confirmBtn().click();
    }
}
