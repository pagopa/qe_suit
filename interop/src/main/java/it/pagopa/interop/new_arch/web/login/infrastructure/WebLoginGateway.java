package it.pagopa.interop.new_arch.web.login.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import it.pagopa.interop.new_arch.web.eservice.infrastructure.suit.EServiceCatalogPage;
import it.pagopa.interop.new_arch.web.infrastructure.WebBrowserGateway;
import it.pagopa.interop.new_arch.web.infrastructure.cucumber.WebBrowserContext;
import it.pagopa.interop.new_arch.web.infrastructure.suit.component.Header;
import it.pagopa.interop.new_arch.web.login.infrastructure.suit.DashboardPage;
import it.pagopa.interop.new_arch.web.login.infrastructure.suit.LoginPage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class WebLoginGateway {
    private final Environment environment;
    private final WebBrowserContext browserContext;

    private final WebBrowserGateway browserGateway;
    private final WebPresentationGateway presentationGateway;

    private final LoginPage loginPage;
    private final DashboardPage dashboardPage;
    private final EServiceCatalogPage eServiceCatalogPage;

    public void login(User user, Tenant tenant) {
        if (isLoggedIn(user, tenant)) return;
        else logout();

        String profiloAttivo = Arrays.stream(environment.getActiveProfiles())
                .findFirst()
                .orElse("qa");

        loginPage.navigateTo();
        loginPage.login(user, tenant);
        dashboardPage.openInterop(profiloAttivo);
        eServiceCatalogPage.assertLoaded();
        browserContext.set(user, tenant);
    }

    public void logout() {
        if (!browserGateway.hasSessionToken()) return;
        Header header = presentationGateway.bind(Header.class);
        header.logout();
        browserContext.logout();
    }

    public boolean isLoggedIn(User user, Tenant tenant) {
        return browserContext.isLoggedIn(user, tenant);
    }

}
