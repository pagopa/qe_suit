package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Page;
import it.frontend.e2e.framework.web.domain.User;

@Url("${url.notifiche.persona-giuridica.base}")
public interface PgLoginPage extends AbstractOneIdPage {


    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div/div/div/div/div/div[2]/div/div")
    Clickable pgSelector();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[3]/div/button")
    Clickable pgLoginButton();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/main/div/div[3]/div[2]/div[8]/div/div/div/div[3]/button")
    Clickable reservedAreaEnvSelector();

    @Override
    default void loginWithSpid(User user) {
        authArea().spidButton().click();
        authArea().providerDialog().selectFakeProvider();
        loginForm().loginWith(user);
        pgSelector().click();
        pgLoginButton().click();
        reservedAreaEnvSelector().click();
    }
}
