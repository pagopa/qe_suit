package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.frontend.e2e.framework.web.domain.User;
import it.pagopa.send.steps.login.component.OneIdLoginForm;
import it.pagopa.send.steps.login.component.OneTrustBanner;

import java.util.Optional;

@Url("ciao")
public interface AbstractOneIdPage extends Page {
    interface AuthArea extends Component {

        interface ProviderDialog extends Component {
            @XPath("//*[@id=\"https://idp.uat.oneid.pagopa.it\"]|//*[@id=\"xx_testenv2\"]|//*[@id=\"spid-select-xx_testenv2\"]")
            Clickable providerButton();

            default void selectFakeProvider() {
                providerButton().click();
            }
        }

        @XPath("//*[@id=\"root\"]/div/div[2]/div[1]/div/h3")
        Readable<String> header();

        @XPath("//*[@id=\"spidButton\"]")
        Clickable spidButton();

        AuthArea.ProviderDialog providerDialog();
    }

    AuthArea authArea();
    OneIdLoginForm loginForm();
    Optional<OneTrustBanner> oneTrustBanner();

    default void loginWithSpid(User user) {
        authArea().spidButton().click();
        authArea().providerDialog().selectFakeProvider();
        loginForm().loginWith(user);
    }
}
