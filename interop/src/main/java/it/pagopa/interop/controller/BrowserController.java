package it.pagopa.interop.controller;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.domain.context.BrowserContext;
import it.pagopa.interop.domain.context.CurrentUserContext;
import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.enums.UserRole;
import it.pagopa.interop.service.browser.WebBrowserService;
import it.pagopa.interop.web.pages.catalog.EServiceCatalogPage;
import it.pagopa.interop.infrastructure.client.auth.bearer.BearerAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BrowserController {

    private final BrowserContext browserContext;
    private final CurrentUserContext currentUserContext;

    private final WebBrowserService webBrowserService;
    private final EServiceCatalogPage eServiceCatalogPage;
    private final BearerAuthProvider bearerAuthProvider;

    @When("l'utente {userRole} di {tenant} si trova alla pagina {page} del portale Interop")
    @When("l'utente {userRole} di {tenant} si trova alla pagina {page} del portale Interop e verifica che tutti gli elementi siano visibili")
    public void navigateToPage(UserRole userRole, Tenant tenant, Page page) {
        User user = User.getTenantUser(tenant, userRole);

        if (!currentUserContext.isLoggedIn(user, tenant) || !webBrowserService.hasSessionToken()) {
            currentUserContext.set(user, tenant);
            browserContext.set(user, tenant);

            String sessionToken = bearerAuthProvider.getToken();

            // Il localStorage è legato all'origin corrente.
            // Se il browser non è ancora sul portale Interop,
            // il token verrebbe salvato sull'origin sbagliato.
            if (browserContext.getCurrentPage() == null) {
                eServiceCatalogPage.navigateTo();
            }

            // Il browser è ora sull'origin corretto del portale.
            webBrowserService.setSessionToken(sessionToken);
        }

        page.navigateTo();
        page.assertLoaded();

        browserContext.setCurrentPage(page);
    }

    @Then("viene mostrata la snackbar con un messaggio di errore contenente {string}")
    public void assertSnackbar(String errorMessage) {
        String actualErrorMessage = webBrowserService.getSnackbarErrorMessage();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nella snackbar")
                .contains(errorMessage);
    }
}
