package it.pagopa.send.steps.login;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.User;
import it.pagopa.send.steps.destinatari.page.ConfigureAddressSendPage;
import it.pagopa.send.steps.login.page.AbstractOneIdPage;
import it.pagopa.send.steps.login.page.DashboardPartySelectionPage;
import it.pagopa.send.steps.login.page.OneIdPage;
import it.pagopa.send.steps.login.page.PfLoginPage;
import it.pagopa.send.steps.login.page.PgLoginPage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginSteps {

    private final WebPresentationGateway uiGateway;

    @Given("{userType} {user} effettua l'accesso a SelfCare con autenticazione SPID")
    public void spidAuth(String userType, User user) {
        AbstractOneIdPage loginPage = switch (userType) {
            case "PA" -> uiGateway.bind(OneIdPage.class);
            case "PG" -> uiGateway.bind(PgLoginPage.class);
            default -> uiGateway.bind(PfLoginPage.class);
        };
        loginPage.navigateTo();
        loginPage.loginWithSpid(user);
    }

    @When("l'utente accede alla dashboard selezionando {string}")
    public void selectPa(String comune) {
        DashboardPartySelectionPage partyPage = uiGateway.bind(DashboardPartySelectionPage.class);
        partyPage.selectComune(comune);
    }

    @And("se presente, viene saltata la configurazione del prodotto SEND")
    public void skipSendConfiguration() {
        try {
            ConfigureAddressSendPage configureAddressSendPage = uiGateway.bind(ConfigureAddressSendPage.class);
            configureAddressSendPage.assertLoaded();
        } catch (RuntimeException e) {
            // Se la pagina non è caricata, significa che non è presente, quindi si può procedere
        }
    }
}
