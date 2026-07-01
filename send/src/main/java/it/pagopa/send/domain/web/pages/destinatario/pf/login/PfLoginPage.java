package it.pagopa.send.domain.web.pages.destinatario.pf.login;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.domain.User;
import it.pagopa.send.domain.web.component.login.OneTrustBanner;
import it.pagopa.send.domain.web.commons.pages.login.AbstractOneIdPage;

@Url("${url.notifiche.cittadino.base}")
public interface PfLoginPage extends AbstractOneIdPage, Page {

    @Override
    default void loginWithSpid(User user) {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        authArea().spidButton().click();
        authArea().providerDialog().selectFakeProvider();
        loginForm().loginWith(user);
    }

}
