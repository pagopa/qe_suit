package it.pagopa.interop.new_arch.web.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.new_arch.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.UserContext;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import it.pagopa.interop.new_arch.common.kernel.domain.UserRole;
import it.pagopa.interop.new_arch.web.eservice.infrastructure.suit.EServiceCatalogPage;
import it.pagopa.interop.new_arch.web.infrastructure.WebBrowserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebBrowserSteps {

    private final WebBrowserContext browserContext;
    private final UserContext userContext;

    private final WebBrowserGateway browserGateway;
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

        if (!userContext.isLoggedIn(user, tenant) || !browserGateway.hasSessionToken()) {
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
            browserGateway.setSessionToken(sessionToken);
        }

        page.navigateTo();
        page.assertLoaded();

        browserContext.setCurrentPage(page);
    }
}
