package it.pagopa.send;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

public interface Header extends Component {

    @XPath("//*[@id=\"root\"]/div[1]/header/div[1]/div/div/div/button")
    Clickable logoutButton();

    interface LogoutOverlay extends Component {

        @XPath("/html/body/div[3]/div[3]/div/div/button[2]")
        Clickable confirmLogoutButton();

        @XPath("//*[@id=\"cancelButton\"]")
        Clickable cancelLogoutButton();
    }

    LogoutOverlay logoutOverlay();

    default void logout() {
        logoutButton().click();
        logoutOverlay().confirmLogoutButton().click();
    }

}
