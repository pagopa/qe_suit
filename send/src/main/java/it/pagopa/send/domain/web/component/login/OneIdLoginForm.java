package it.pagopa.send.domain.web.component.login;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.domain.User;

public interface OneIdLoginForm extends Component {

    @XPath("//*[@id=\"username\"]")
    Writable<String> username();

    @XPath("//*[@id=\"password\"]")
    Writable<String> password();

    @XPath("//*[@id=\"login-form\"]/div[4]/button[1]|" +
            "/html/body/section/main/article/form/div[3]/button[1]|" +
            "//*[@id=\"formLogin\"]/button")
    Clickable submit();

    OneIdPrivacyDialog oneIdPrivacyDialog();

    default void loginWith(User user) {
        this.username().writeAndAssert(user.getUsername());
        this.password().writeAndAssert(user.getPassword());
        this.submit().click();

        oneIdPrivacyDialog().accept();
    }
}



