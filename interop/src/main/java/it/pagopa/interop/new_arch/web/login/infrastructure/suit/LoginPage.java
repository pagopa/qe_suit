package it.pagopa.interop.new_arch.web.login.infrastructure.suit;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import it.pagopa.interop.new_arch.web.infrastructure.component.Button;
import it.pagopa.interop.new_arch.web.login.infrastructure.suit.component.LoginForm;
import it.pagopa.interop.new_arch.web.login.infrastructure.suit.component.OneTrustBanner;
import it.pagopa.interop.new_arch.web.login.infrastructure.suit.component.PrivacyDialog;
import it.pagopa.interop.new_arch.web.login.infrastructure.suit.component.ProviderDialog;

import java.util.Optional;

@Url("https://uat.oneid.pagopa.it/login?response_type=CODE&scope=openid&client_id=0GemhuNwzjygMbWHJjYCMInHkYInwDjax7xQ-lFqiUs&state=d2a4636b98774b9&nonce=c726266cb488457&redirect_uri=https%3A%2F%2Fuat.selfcare.pagopa.it%2Fauth%2Flogin%2Fcallback")
public interface LoginPage extends Page {

    @XPath("//*[@id=\"spidButton\"]")
    Button spidButton();

    ProviderDialog providerDialog();

    PrivacyDialog privacyDialog();

    LoginForm loginForm();

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@id=\"root\"]/div/div[1]/div/div/div[4]/div/button")
    Optional<Button> retryButton();

    @XPath("//*[@id=\"search\"]")
    Writable<String> tenantInput();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div/div/div/div[2]/div/div/div/div")
    Button tenantOption();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[3]/div/button")
    Button loginButton();

    default void login(User user, Tenant tenant) {
        spidButton().click();
        providerDialog().selectFakeProvider();
        loginForm().loginWith(user.getUsername(), user.getPassword());
        privacyDialog().accept();
        oneTrustBanner().ifPresent(OneTrustBanner::accept);

        // In ambiente di QA, per ogni nuovo browser aperto, il login avrà successo solo al secondo tentativo. Il problema è noto ai devs
        retryButton().ifPresent(btn -> {
            btn.click();
            spidButton().click();
            providerDialog().selectFakeProvider();
            loginForm().loginWith(user.getUsername(), user.getPassword());
            privacyDialog().accept();
        });

        tenantInput().writeAndAssert(tenant.getName());
        tenantOption().click();
        loginButton().click();
    }
}
