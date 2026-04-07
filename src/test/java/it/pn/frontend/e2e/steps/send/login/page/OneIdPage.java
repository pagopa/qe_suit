package it.pn.frontend.e2e.steps.send.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pn.frontend.e2e.enums.User;
import it.pn.frontend.e2e.steps.send.login.component.OneIdLoginForm;

@Url("${url.selfcare.notifiche.base}")
public interface OneIdPage extends Page {

    interface AuthArea extends Component {

        interface ProviderDialog extends Component {
            @XPath("//*[@id=\"https://idp.uat.oneid.pagopa.it\"]")
            Clickable providerButton();

            default void selectFakeProvider() {
                providerButton().click();
            }
        }

        @XPath("//*[@id=\"root\"]/div/div[2]/div[1]/div/h3")
        Readable<String> header();

        @XPath("//*[@id=\"root\"]/div/div[2]/div[3]/div[1]")
        Clickable spidButton();

        ProviderDialog providerDialog();
    }

    AuthArea authArea();
    OneIdLoginForm loginForm();

    default void loginWithSpid(User user) {
        authArea().spidButton().click();
        authArea().providerDialog().selectFakeProvider();
        loginForm().loginWith(user);
    }
}
