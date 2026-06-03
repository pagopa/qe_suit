package it.pagopa.interop.ui.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

public interface Header extends Component {

    @XPath("//*[@id=\"root\"]/div/div[1]/header/div/div/div/div/button[3]")
    Button logoutButton();

    @XPath("/html/body/div[3]/div[3]/div/div/div[2]/div[2]/div[2]/button")
    Button logoutConfirmButton();

    default void logout(){
        logoutButton().click();
        logoutConfirmButton().click();
    }
}
