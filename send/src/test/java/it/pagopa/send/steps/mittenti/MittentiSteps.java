package it.pagopa.send.steps.mittenti;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.steps.FakeAuthenticator;
import it.pagopa.send.steps.IAuthenticator;
import it.pagopa.send.steps.login.component.OneTrustBanner;
import it.pagopa.send.steps.login.page.ReserverdAreaPage;
import it.pagopa.send.steps.mittenti.components.NotificationRow;
import it.pagopa.send.steps.mittenti.creazione_notifica.NotificationContext;
import it.pagopa.send.steps.mittenti.pages.DashboardPage;
import lombok.RequiredArgsConstructor;
import it.pagopa.send.steps.mittenti.components.NotificationRow;
import java.util.List;

import org.junit.jupiter.api.Assertions;

@RequiredArgsConstructor
public class MittentiSteps {
    private final NotificationContext context; 
    private final WebPresentationGateway browser;
    private Page currentPage;

    @Given("l'utente è un {string} di {string}")
    public void login(String role, String pa) {
        //Inizializzare un bean Auth
        //Auth auth = Auth.of(role,pa,fake);
        IAuthenticator auth = new FakeAuthenticator(role, pa);
        Assertions.assertTrue(auth.isAuthenticated());
    }

    @When("naviga alla pagina {page}")
    public void navigateTo(Class<? extends Page> page) {
        currentPage = browser.bind(page);
        currentPage.navigateTo();

        try {
            browser.bind(OneTrustBanner.class).accept();
        } catch (Exception e) {
            //log.debug("OneTrust banner not found - continuing");
        }
    }

    @Then("la pagina deve caricarsi correttamente")
    public void laPaginaDeveCaricarsiCorrettamente() {
       currentPage.assertLoaded();
    }

    @When("l'utente clicca su Crea Notifica")
    public void creaNotifica() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.assertLoaded();
        dashboardPage.clickCreaNotifica();
    }

    @When("filtra per {word} con valore {string}")
    public void filterBy(String tipoFiltro, String valore) {

        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().filterBy(tipoFiltro, valore);
        dashboardPage.assertLoaded();
    }

    @Given("leggo la prima notifica in lista")
    public void leggiPrimaNotifica() {
        DashboardPage dashboard = browser.bind(DashboardPage.class);
        dashboard.waitUntilReady();
        List<NotificationRow> rows = dashboard.rows();
        org.junit.jupiter.api.Assertions.assertFalse(rows.isEmpty(), "Nessuna notifica in lista");
        NotificationRow first = rows.get(0);
        context.setIun(first.iun().read().getText());
        context.setRecipientTaxCode(first.recipient().read().getText());
    }

    @When("filtra per IUN dalla notifica letta")
    public void filterByIunFromContext() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().filterBy("IUN", context.getIun());
        dashboardPage.assertLoaded();
    }

    @When("filtra per TAX_CODE dalla notifica letta")
    public void filterByTaxCodeFromContext() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.removeFiltersIfPresent();
        dashboardPage.waitUntilReady();
        dashboardPage.filters().filterBy("TAX_CODE", context.getRecipientTaxCode());
        dashboardPage.assertLoaded();
    }

    @Then("i risultati contengono l'IUN della notifica letta")
    public void verificaRisultatiIun() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.waitUntilReady();
        List<NotificationRow> rows = dashboardPage.rows();
        Assertions.assertEquals(1, rows.size(), "Attesa esattamente 1 notifica per IUN univoco");
        Assertions.assertEquals(context.getIun(), rows.get(0).iun().read().getText());
    }

    @Then("i risultati contengono il TAX_CODE della notifica letta")
    public void verificaRisultatiTaxCode() {
        DashboardPage dashboardPage = browser.bind(DashboardPage.class);
        dashboardPage.waitUntilReady();
        List<NotificationRow> rows = dashboardPage.rows();
        Assertions.assertFalse(rows.isEmpty(), "Nessun risultato trovato");
        long matching = rows.stream()
            .filter(row -> row.recipient().read().getText().contains(context.getRecipientTaxCode()))
            .count();
        Assertions.assertEquals(rows.size(), (int) matching,
            "Non tutte le notifiche corrispondono al TAX_CODE filtrato");
    }
}
