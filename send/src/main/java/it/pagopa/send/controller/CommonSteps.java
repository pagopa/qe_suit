package it.pagopa.send.controller;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.domain.web.commons.component.Footer;
import it.pagopa.send.domain.web.commons.component.Header;
import it.pagopa.send.domain.web.commons.component.Sidebar;
import it.pagopa.send.domain.web.commons.pages.login.AbstractComunePickerPage;
import it.pagopa.send.domain.web.pages.supporto.BackstageProfilePage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonSteps {
    private final WebPresentationGateway uiGateway;
    private Page currentPage;

    @When("si passa alla sezione {string} tramite la sidebar")
    public void navigateToSection(String section) {
        Sidebar sidebar = uiGateway.bind(Sidebar.class);
        sidebar.goToSection(section);
    }

    @When("la lingua della pagina viene impostata su {string}")
    public void setLanguage(String language) {
        Footer footer = uiGateway.bind(Footer.class);
        footer.changeLanguage(language);
    }

    @When("la pagina {page} è caricata con successo")
    public void assertPageLoaded(Class<? extends Page> page) {
        Page pageInstance = uiGateway.bind(page);
        pageInstance.assertLoaded();
    }

    @When("l'utente accede alla {page} selezionando {string}")
    public void selectPa(Class<? extends Page> page, String comune) {
        AbstractComunePickerPage comunePickerPage = switch (page.getSimpleName()) {
            case "BackstageProfilePage" -> uiGateway.bind(BackstageProfilePage.class);
            default -> throw new IllegalArgumentException("Pagina non riconosciuta: " + page);
        };
        comunePickerPage.selectComune(comune);
    }

    @And("viene effettuato il logout")
    public void logout() {
        Header footer = uiGateway.bind(Header.class);
        footer.logout();
    }

    @When("naviga alla pagina {page}")
    public void navigateTo(Class<? extends Page> page) {
        currentPage = uiGateway.bind(page);
        currentPage.navigateTo();
    }

    @Then("la pagina deve caricarsi correttamente")
    public void laPaginaDeveCaricarsiCorrettamente() {
        currentPage.assertLoaded();
    }
}
