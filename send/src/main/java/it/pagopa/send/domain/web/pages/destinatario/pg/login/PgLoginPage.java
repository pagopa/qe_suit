package it.pagopa.send.domain.web.pages.destinatario.pg.login;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.common.kernel.domain.User;
import it.pagopa.send.domain.web.commons.pages.login.AbstractOneIdPage;

@Url("${url.notifiche.persona-giuridica.base}")
public interface PgLoginPage extends AbstractOneIdPage, Page {

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div/div/div/div/div/div[2]/div/div")
    Clickable pgSelector();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[3]/div/button")
    Clickable pgLoginButton();

    @XPath("${selettore.login.pg.area-riservata-selettore-env}")
    Clickable reservedAreaEnvSelector();

    @Override
    default void loginWithSpid(User user) {
        AbstractOneIdPage.super.loginWithSpid(user);
        pgSelector().click();
        pgLoginButton().click();
        reservedAreaEnvSelector().click();
    }
}
