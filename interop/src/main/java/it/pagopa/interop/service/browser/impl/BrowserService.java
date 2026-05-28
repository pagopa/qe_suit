package it.pagopa.interop.service.browser.impl;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.domain.context.BrowserContext;
import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.service.browser.WebBrowserService;
import it.pagopa.interop.web.commons.component.Header;
import it.pagopa.interop.web.commons.component.Snackbar;
import it.pagopa.interop.web.pages.catalog.EServiceCatalogPage;
import it.pagopa.interop.web.pages.login.DashboardPage;
import it.pagopa.interop.web.pages.login.LoginPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BrowserService implements WebBrowserService {

    private final WebPresentationGateway webPresentationGateway;
    private final LoginPage loginPage;
    private final DashboardPage dashboardPage;
    private final EServiceCatalogPage eServiceCatalogPage;
    private final BrowserContext browserContext;
    private final Environment environment;

    @Value("${interop.web.base-url}")
    private String baseUrl;

    @Override
    public boolean hasSessionToken() {
        try {
            return webPresentationGateway.getLocalStorageItem("token").isPresent();
        } catch (RuntimeException ex) {
            // localStorage non accessibile (es. data:/about:blank)
            return false;
        }
    }

    @Override
    public void setSessionToken(String token) {
        try {
            webPresentationGateway.setLocalStorageItem("token", token);
        } catch (RuntimeException firstFailure) {
            // Fallback robusto: porta il browser su origin http(s), poi riprova
            webPresentationGateway.navigateTo(Url.of(baseUrl));
            webPresentationGateway.setLocalStorageItem("token", token);
        }
    }

    @Override
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

    @Override
    public void logout() {
        if (!hasSessionToken()) return;
        Header header = webPresentationGateway.bind(Header.class);
        header.logout();
        browserContext.logout();
    }

    @Override
    public boolean isLoggedIn(User user, Tenant tenant) {
        return browserContext.isLoggedIn(user, tenant);
    }

    @Override
    public String getSnackbarErrorMessage() {
        Snackbar snackbar = webPresentationGateway.bind(Snackbar.class);

        if (!snackbar.alert().isError())
            throw new IllegalStateException("Snackbar non in stato di errore, impossibile leggere il messaggio");

        return snackbar.alert().message().read();
    }
}