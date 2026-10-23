package it.pagopa.send.web.mittente.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MittentiSteps {

    private final WebPresentationGateway browser;
    private final WebBrowserContext webBrowserContext;
//    private final DashboardPage dashboardPage;
    private Page currentPage;

    // l'ente diventa l'utente del browser: le pagine si aprono con la sua sessione nel sessionStorage
    @Given("l'utente è un {string} di {user}")
    public void login(String role, User tenant) {
        webBrowserContext.setTenant(tenant);
        webBrowserContext.setCurrentUser(tenant);
    }

    @When("naviga alla pagina {page}")
    public void navigateTo(Class<? extends Page> page) {
        currentPage = browser.bind(page);
        currentPage.navigateTo();
        currentPage.assertLoaded();
        webBrowserContext.setCurrentPage(currentPage);
    }

    @Then("la pagina deve caricarsi correttamente")
    public void laPaginaDeveCaricarsiCorrettamente() {
       currentPage.assertLoaded();
    }

}
