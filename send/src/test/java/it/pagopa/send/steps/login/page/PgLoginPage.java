package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.frontend.e2e.framework.web.domain.SpidLoginPage;
import it.frontend.e2e.framework.web.domain.User;
import it.pagopa.send.steps.login.component.OneIdLoginForm;

@Url("${url.notifiche.persona-giuridica.base}")
public interface PgLoginPage extends Page, SpidLoginPage {

    interface AuthArea extends Component {
        // Dialog per la selezione del provider SPID, in questo caso fittizio per i test
        interface ProviderDialog extends Component {
            @XPath("//*[@id=\"xx_testenv2\"]")
            Clickable providerButton();

            default void selectFakeProvider() {
                providerButton().click();
            }
        }

        @XPath("//*[@id=\"spidButton\"]")
        Clickable spidButton();

        ProviderDialog providerDialog();
    }

    AuthArea authArea();
    OneIdLoginForm loginForm();

    @Override
    default void loginWithSpid(User user) {
        authArea().spidButton().click();
        authArea().providerDialog().selectFakeProvider();
        loginForm().loginWith(user);
    }
}
