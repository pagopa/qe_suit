package it.pagopa.interop.controller;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.enums.UserRole;
import it.pagopa.interop.domain.services.browser.WebBrowserService;
import it.pagopa.interop.infrastructure.client.auth.bearer.BearerAuthProvider;
import it.pagopa.interop.infrastructure.client.auth.context.user.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BrowserController {

    private final WebBrowserService webBrowserService;
    private final CurrentUserContext currentUserContext;
    private final BearerAuthProvider bearerAuthProvider;

    @When("l'utente {userRole} di {tenant} si trova alla pagina {page} del portale Interop")
    @When("l'utente {userRole} di {tenant} si trova alla pagina {page} del portale Interop e verifica che tutti gli elementi siano visibili")
    public void navigateToPage(UserRole userRole, Tenant tenant, Page page) {
        User user = User.getTenantUser(tenant, userRole);

        if (!currentUserContext.isLoggedIn(user, tenant) || !webBrowserService.hasSessionToken()) {
            currentUserContext.set(user, tenant);
            String sessionToken = bearerAuthProvider.getToken();
            webBrowserService.setSessionToken(sessionToken);
        }

        page.navigateTo();
        page.assertLoaded();
    }

    @Then("viene mostrata la snackbar con un messaggio di errore contenente {string}")
    public void assertSnackbar(String errorMessage) {
        String actualErrorMessage = webBrowserService.getSnackbarErrorMessage();

        assertThat(actualErrorMessage)
                .as("Messaggio di errore visualizzato nella snackbar")
                .contains(errorMessage);
    }
}
