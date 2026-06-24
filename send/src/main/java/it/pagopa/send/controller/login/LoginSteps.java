package it.pagopa.send.controller.login;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.domain.User;
import it.pagopa.send.domain.context.CurrentUserContext;
import it.pagopa.send.domain.web.pages.destinatario.ConfigureAddressSendPage;
import it.pagopa.send.domain.web.commons.pages.login.AbstractOneIdPage;
import it.pagopa.send.domain.web.commons.pages.login.OneIdPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.login.PfLoginPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.login.PgLoginPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoginSteps {

    private final WebPresentationGateway uiGateway;
    private final CurrentUserContext currentUserContext;

    @Given("{userType} {user} effettua l'accesso a SelfCare con autenticazione SPID")
    public void spidAuth(String userType, User user) {
        currentUserContext.set(user);
        AbstractOneIdPage loginPage = switch (userType) {
            case "PA" -> uiGateway.bind(OneIdPage.class);
            case "PG" -> uiGateway.bind(PgLoginPage.class);
            default -> uiGateway.bind(PfLoginPage.class);
        };
        loginPage.navigateTo();
        loginPage.loginWithSpid(user);
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
