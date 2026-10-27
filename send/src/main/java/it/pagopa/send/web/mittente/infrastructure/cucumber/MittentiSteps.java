package it.pagopa.send.web.mittente.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MittentiSteps {

    private final WebPresentationGateway browser;
    private final WebBrowserContext webBrowserContext;

    @When("naviga alla pagina {page}")
    public void navigateTo(Class<? extends Page> page) {
        Page currentPage = browser.bind(page);
        currentPage.navigateTo();
        currentPage.assertLoaded();
        webBrowserContext.setCurrentPage(currentPage);
    }

    @Then("la pagina deve caricarsi correttamente")
    public void laPaginaDeveCaricarsiCorrettamente() {
        webBrowserContext.getCurrentPage().assertLoaded();
    }

}
