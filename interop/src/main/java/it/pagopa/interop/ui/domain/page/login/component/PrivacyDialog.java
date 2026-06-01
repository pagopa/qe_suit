package it.pagopa.interop.ui.domain.page.login.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.ui.domain.component.Button;

public interface PrivacyDialog extends Component {
    @XPath("//*[@id=\"consent-form\"]/div[2]/button[2]")
    Button acceptButton();

    @XPath("//*[@id=\"consent-form\"]/div[2]/button[1]")
    Button rejectButton();

    default void accept() {
        acceptButton().click();
    }

    default void reject() {
        rejectButton().click();
    }
}
