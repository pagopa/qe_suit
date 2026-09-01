package it.pagopa.interop.web.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebBrowserSteps {

    private final WebBrowserContext browserContext;
    private final CurrentUserSession currentUserSession;

    @When("un {userRole} di {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("un {userRole} del {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("un utente {userRole} di {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("un utente {userRole} del {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("l'utente {userRole} di {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    @When("l'utente {userRole} del {tenant} si trova alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    public void navigateToPage(UserRole userRole, Tenant tenant, Page page) {
        User user = User.getTenantUser(tenant, userRole);

        if (!currentUserSession.isLoggedIn(user, tenant)) {
            currentUserSession.set(user, tenant);
        }

        navigateToPage(page);
    }

    @When("l'utente naviga alla pagina {page}( e verifica che tutti gli elementi siano visibili)")
    @When("l'utente naviga alla pagina {page} del portale Interop( e verifica che tutti gli elementi siano visibili)")
    public void navigateToPage(Page page) {
        page.navigateTo();
        page.assertLoaded();

        browserContext.setCurrentUrl(page.getUrl());
    }
}
