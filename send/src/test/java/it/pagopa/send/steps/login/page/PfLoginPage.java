package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.frontend.e2e.framework.web.domain.User;
import it.pagopa.send.steps.login.component.OneTrustBanner;

@Url("${url.notifiche.cittadino.base}")
public interface PfLoginPage extends AbstractOneIdPage, Page {

    OneTrustBanner oneTrustBanner();

    @Override
    default void loginWithSpid(User user) {
        oneTrustBanner().accept();
        authArea().spidButton().click();
        authArea().providerDialog().selectFakeProvider();
        loginForm().loginWith(user);
    }

}
