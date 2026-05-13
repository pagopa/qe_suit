package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.Property;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Page;
import it.frontend.e2e.framework.web.domain.User;
import it.pagopa.send.steps.login.component.OneTrustBanner;

@Url("${url.notifiche.persona-giuridica.base}")
public interface PgLoginPage extends AbstractOneIdPage, Page {

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div/div/div/div/div/div[2]/div/div")
    Clickable pgSelector();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[3]/div/button")
    Clickable pgLoginButton();

    @Property("selectors.login.pg.reserved-area-env-selector")
    Clickable reservedAreaEnvSelector();

    @Override
    default void loginWithSpid(User user) {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        authArea().spidButton().click();
        authArea().providerDialog().selectFakeProvider();
        loginForm().loginWith(user);
        pgSelector().click();
        pgLoginButton().click();
        reservedAreaEnvSelector().click();
    }
}
