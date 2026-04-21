package it.pagopa.send.steps;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.Footer;
import it.pagopa.send.Sidebar;
import it.pagopa.send.steps.login.page.AbstractComunePickerPage;
import it.pagopa.send.steps.login.page.DashboardPartySelectionPage;
import it.pagopa.send.steps.supporto.BackstageProfilePage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommonSteps {
    private final WebPresentationGateway uiGateway;

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
            case "DashboardPage" -> uiGateway.bind(DashboardPartySelectionPage.class);
            case "BackstageProfilePage" -> uiGateway.bind(BackstageProfilePage.class);
            default -> throw new IllegalArgumentException("Pagina non riconosciuta: " + page);
        };
        comunePickerPage.selectComune(comune);
    }


}
