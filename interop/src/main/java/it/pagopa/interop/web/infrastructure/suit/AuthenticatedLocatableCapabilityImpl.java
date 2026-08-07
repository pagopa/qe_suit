package it.pagopa.interop.web.infrastructure.suit;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.impl.LocatableCapabilityImpl;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.common.infrastructure.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.infrastructure.cucumber.WebBrowserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedLocatableCapabilityImpl extends LocatableCapabilityImpl {

    @Value("${interop.web.catalog}")
    private String catalogUrl;

    private final IWebPresentationApiAdapter adapter;
    private final WebBrowserContext webBrowserContext;
    private final CurrentUserSession currentUserSession;
    private final BearerAuthProvider bearerAuthProvider;

    public AuthenticatedLocatableCapabilityImpl(
            IWebPresentationApiAdapter adapter,
            WebBrowserContext webBrowserContext,
            CurrentUserSession currentUserSession,
            BearerAuthProvider bearerAuthProvider) {

        super(adapter);
        this.adapter = adapter;
        this.webBrowserContext = webBrowserContext;
        this.currentUserSession = currentUserSession;
        this.bearerAuthProvider = bearerAuthProvider;
    }

    @Override
    public void navigateTo() {
        User currentUser = currentUserSession.getUser();
        Tenant currentTenant = currentUserSession.getTenant();
        String sessionToken = bearerAuthProvider.getToken(currentUser, currentTenant);

        // Il localStorage è legato all'origin corrente.
        // Se il browser non è ancora sul portale Interop,
        // il token verrebbe salvato sull'origin sbagliato.
        if (webBrowserContext.getCurrentUrl() == null)
            adapter.navigateTo(Url.of(catalogUrl));

        // Il browser è ora sull'origin corretto del portale
        adapter.setLocalStorageItem("token", sessionToken);

        Url targetUrl = urlSupplier.get();
        adapter.navigateTo(targetUrl);
        webBrowserContext.setCurrentUrl(targetUrl);
    }
}
