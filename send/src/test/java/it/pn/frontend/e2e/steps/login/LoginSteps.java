package it.pn.frontend.e2e.steps.login;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pn.frontend.e2e.enums.User;
import it.pn.frontend.e2e.steps.login.page.DashboardPartySelectionPage;
import it.pn.frontend.e2e.steps.login.page.OneIdPage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginSteps {

    private final WebPresentationGateway uiGateway;

    @Given("l'utente {user} effettua l'accesso a SelfCare con autenticazione SPID")
    public void spidAuth(User user) {
        OneIdPage oneId = uiGateway.bind(OneIdPage.class);
        oneId.navigateTo();
        oneId.loginWithSpid(user);
    }

    @When("l'utente accede alla dashboard selezionando {string}")
    public void selectPa(String comune) {
        DashboardPartySelectionPage partyPage = uiGateway.bind(DashboardPartySelectionPage.class);
        partyPage.selectComune(comune);
    }
}
