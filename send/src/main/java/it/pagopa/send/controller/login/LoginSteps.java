package it.pagopa.send.controller.login;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.kernel.domain.Recipient;
import it.pagopa.send.common.kernel.domain.Tenant;
import it.pagopa.send.domain.web.pages.destinatario.ConfigureAddressSendPage;
import it.pagopa.send.web.login.infrastructure.LoginActions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoginSteps {

    private final WebPresentationGateway uiGateway;
    private final LoginActions loginActions;

    @Given("la PA {tenant} effettua l'accesso a SelfCare con autenticazione SPID")
    public void spidAuthPa(Tenant tenant) {
        loginActions.loginAsTenant(tenant);
    }

    @Given("{recipientType} {recipient} effettua l'accesso a SelfCare con autenticazione SPID")
    public void spidAuthRecipient(String userType, Recipient recipient) {
        loginActions.loginAsRecipient(userType, recipient);
    }

    @Given("la PA {tenant} è loggata a SEND")
    public void forceTenantSession(Tenant tenant) {
        loginActions.forceTenantSession(tenant);
    }

    @Given("{recipientType} {recipient} forza l'accesso a SelfCare tramite sessionStorage")
    public void forceRecipientSession(String userType, Recipient recipient) {
        loginActions.forceRecipientSession(userType, recipient);
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
