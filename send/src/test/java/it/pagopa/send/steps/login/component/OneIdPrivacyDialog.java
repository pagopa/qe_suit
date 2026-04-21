package it.pagopa.send.steps.login.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

public interface OneIdPrivacyDialog extends Component {

    @XPath("/div[2]/button[2]|/html/body/section/main/article/form/div/button[1]")
    Clickable acceptButton();

    @XPath("/div[2]/button[1]|/html/body/section/main/article/form/div/button[2]")
    Clickable rejectButton();

    default void accept(){
        acceptButton().click();
    }

    default void reject(){
        rejectButton().click();
    }
}
