package it.pagopa.send.steps.login;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.enums.User;
import it.pagopa.send.steps.login.page.DashboardPartySelectionPage;
import it.pagopa.send.steps.login.page.OneIdPage;
import it.pagopa.send.steps.login.page.ReserverdAreaPage;
import it.pagopa.send.steps.mittenti.creazione_notifica.NotificationContext;
import it.pagopa.send.steps.mittenti.pages.DashboardPage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginSteps {
    private final NotificationContext context;
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

    @When("l'utente accede alla area riservata e seleziona il prodotto SEND")
    public void selectSEND() {
        ReserverdAreaPage nextPage = uiGateway.bind(ReserverdAreaPage.class);
        nextPage.assertLoaded();
        nextPage.accediToSend();
        DashboardPage page = uiGateway.bind(DashboardPage.class);
        page.assertLoadedWithBannerCheck();
    }
}
