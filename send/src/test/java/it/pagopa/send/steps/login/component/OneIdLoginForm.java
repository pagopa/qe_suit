package it.pagopa.send.steps.login.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.enums.User;

import java.util.Optional;

public interface OneIdLoginForm extends Component {

    @XPath("//*[@id=\"username\"]")
    Writable<String> username();

    @XPath("//*[@id=\"password\"]")
    Writable<String> password();

    @XPath("//*[@id=\"login-form\"]/div[4]/button[1]")
    Clickable submit();

    @XPath("//*[@id=\"consent-form\"]")
    OneIdPrivacyDialog oneIdPrivacyDialog();

    Optional<OneTrustBanner> oneTrustBanner();

    default void loginWith(User user) {
        this.username().writeAndAssert(user.getUsername());
        this.password().writeAndAssert(user.getPassword());
        this.submit().click();

        oneIdPrivacyDialog().accept();
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
    }
}



