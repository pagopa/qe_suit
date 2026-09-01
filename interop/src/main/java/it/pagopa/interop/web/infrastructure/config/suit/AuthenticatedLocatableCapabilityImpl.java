package it.pagopa.interop.web.infrastructure.config.suit;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.impl.LocatableCapabilityImpl;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cucumber")
public class AuthenticatedLocatableCapabilityImpl extends LocatableCapabilityImpl {

    private final IWebPresentationApiAdapter adapter;
    private final BrowserContext webBrowserContext;
    private final CurrentUserSession currentUserSession;
    private final BearerAuthProvider bearerAuthProvider;
    private final String catalogUrl;

    public AuthenticatedLocatableCapabilityImpl(
            IWebPresentationApiAdapter adapter,
            BrowserContext webBrowserContext,
            CurrentUserSession currentUserSession,
            BearerAuthProvider bearerAuthProvider,
            @Value("${interop.web.catalog}") String catalogUrl
    ) {

        super(adapter);
        this.adapter = adapter;
        this.webBrowserContext = webBrowserContext;
        this.currentUserSession = currentUserSession;
        this.bearerAuthProvider = bearerAuthProvider;
        this.catalogUrl = catalogUrl;
    }

    @Override
    public void navigateTo(String... pathParams) {
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

        Url targetUrl = resolveUrl(pathParams, urlSupplier.get().getUrl());

        adapter.navigateTo(targetUrl);
        webBrowserContext.setCurrentUrl(targetUrl);
    }
}
