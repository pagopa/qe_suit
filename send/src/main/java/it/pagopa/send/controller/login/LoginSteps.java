package it.pagopa.send.controller.login;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.domain.web.pages.destinatario.ConfigureAddressSendPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.NotificationPage;
import it.pagopa.send.domain.web.pages.mittente.DashboardPage;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import it.pagopa.send.web.login.infrastructure.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoginSteps {

    private final WebPresentationGateway uiGateway;
    private final LoginService loginService;
    private final WebBrowserContext webBrowserContext;
    private final DashboardPage dashboardPage;
    private final NotificationPage notificationPage;
    private final NotificationPFPage notificationPFPage;

    @Given("la PA {tenant} effettua l'accesso a SelfCare con autenticazione SPID")
    public void spidAuthPa(Tenant tenant) {
        loginService.loginAsTenant(tenant);
    }

    @Given("{recipientType} {recipient} effettua l'accesso a SelfCare con autenticazione SPID")
    public void spidAuthRecipient(String userType, Recipient recipient) {
        loginService.loginAsRecipient(userType, recipient);
    }

    @Given("la PA {tenant} è loggata a SEND")
    public void forceTenantSession(Tenant tenant) {
        webBrowserContext.setTenant(tenant);
        webBrowserContext.setCurrentUser(tenant);

        dashboardPage.navigateTo();
        dashboardPage.assertLoaded();
    }

    @Given("{recipientType} {recipient} forza l'accesso a SelfCare tramite sessionStorage")
    public void forceRecipientSession(String userType, Recipient recipient) {
        webBrowserContext.setRecipient(recipient);
        webBrowserContext.setCurrentUser(recipient);

        Page landingPage = "PG".equals(userType) ? notificationPage : notificationPFPage;
        landingPage.navigateTo();
        landingPage.assertLoaded();
    }

    @And("se presente, viene saltata la configurazione del prodotto SEND")
    public void skipSendConfiguration() {
        try {
            ConfigureAddressSendPage configureAddressSendPage = uiGateway.bind(ConfigureAddressSendPage.class);
            configureAddressSendPage.assertLoaded();
            configureAddressSendPage.clickSkipConfigButton();
        } catch (AssertionError e) {
            // Se la pagina non è caricata, significa che non è presente, quindi si può procedere
        }
    }
}
