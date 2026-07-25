package it.pagopa.interop.new_arch.web.login.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.new_arch.web.infrastructure.component.Button;

public interface LoginForm extends Component {
    @XPath("//*[@id=\"username\"]")
    Writable<String> username();

    @XPath("//*[@id=\"password\"]")
    Writable<String> password();

    @XPath("//*[@id=\"login-form\"]/div[4]/button[1]|" +
            "/html/body/section/main/article/form/div[3]/button[1]|" +
            "//*[@id=\"formLogin\"]/button")
    Button submit();

    default void loginWith(String username, String password) {
        this.username().writeAndAssert(username);
        this.password().writeAndAssert(password);
        this.submit().click();
    }
}
