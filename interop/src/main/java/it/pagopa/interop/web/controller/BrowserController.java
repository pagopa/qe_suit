package it.pagopa.interop.web.controller;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.common.infrastructure.client.auth.bearer.BearerAuthProvider;
import it.pagopa.interop.common.cucumber.context.UserContext;
import it.pagopa.interop.common.enums.Tenant;
import it.pagopa.interop.common.enums.User;
import it.pagopa.interop.common.enums.UserRole;
import it.pagopa.interop.web.domain.context.BrowserContext;
import it.pagopa.interop.web.page.catalog.EServiceCatalogPage;
import it.pagopa.interop.web.service.BrowserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BrowserController {

    private final BrowserContext browserContext;
    private final UserContext userContext;

    private final BrowserService webBrowserService;
    private final EServiceCatalogPage eServiceCatalogPage;
    private final BearerAuthProvider bearerAuthProvider;

    @When("un {userRole} di {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("un {userRole} del {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("un utente {userRole} di {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("un utente {userRole} del {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("l'utente {userRole} di {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("l'utente {userRole} del {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    public void navigateToPage(UserRole userRole, Tenant tenant, Page page) {
        User user = User.getTenantUser(tenant, userRole);

        if (!userContext.isLoggedIn(user, tenant) || !webBrowserService.hasSessionToken()) {
            userContext.set(user, tenant);
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
}
