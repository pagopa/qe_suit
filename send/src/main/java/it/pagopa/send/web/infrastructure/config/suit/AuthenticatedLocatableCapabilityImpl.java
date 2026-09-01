package it.pagopa.send.web.infrastructure.config.suit;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.impl.LocatableCapabilityImpl;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.domain.User;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import it.pagopa.send.web.login.infrastructure.SelfCareSessionPayloadFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedLocatableCapabilityImpl extends LocatableCapabilityImpl {

    private static final String SESSION_STORAGE_KEY = "user";
    private static final String SESSION_TOKEN_PROPERTY = "token.session.%s";

    private final IWebPresentationApiAdapter adapter;
    private final WebBrowserContext webBrowserContext;
    private final SelfCareSessionPayloadFactory sessionPayloadFactory;
    private final Environment environment;

    public AuthenticatedLocatableCapabilityImpl(
            IWebPresentationApiAdapter adapter,
            WebBrowserContext webBrowserContext,
            SelfCareSessionPayloadFactory sessionPayloadFactory,
            Environment environment
    ) {
        super(adapter);
        this.adapter = adapter;
        this.webBrowserContext = webBrowserContext;
        this.sessionPayloadFactory = sessionPayloadFactory;
        this.environment = environment;
    }

    @Override
    public void navigateTo(String... pathParams) {
        User currentUser = webBrowserContext.getCurrentUser();
        Url targetUrl = urlSupplier.get();

        // Nessun utente noto: pagine del flusso SPID reale (OneIdPage, PfLoginPage, ...),
        // navigazione semplice senza toccare il sessionStorage.
        if (currentUser == null) {
            adapter.navigateTo(targetUrl);
            webBrowserContext.setCurrentUrl(targetUrl);
            return;
        }

        String sessionToken = sessionTokenFor(currentUser);
        String sessionPayload = buildSessionPayload(currentUser, sessionToken);

        // Il sessionStorage è legato all'origin corrente. Se il browser non è ancora atterrato
        // sul portale, il payload verrebbe scritto sull'origin sbagliato.
        if (webBrowserContext.getCurrentUrl() == null) {
            adapter.navigateTo(targetUrl);
        }

        adapter.setSessionStorageItem(SESSION_STORAGE_KEY, sessionPayload);

        adapter.navigateTo(targetUrl);
        webBrowserContext.setCurrentUrl(targetUrl);
    }

    private String sessionTokenFor(User user) {
        return environment.getProperty(SESSION_TOKEN_PROPERTY.formatted(user.getUsername().toLowerCase()));
    }

    private String buildSessionPayload(User user, String sessionToken) {
        if (user instanceof Tenant tenant) {
            return sessionPayloadFactory.buildForTenant(tenant, sessionToken);
        }
        if (user instanceof Recipient recipient) {
            return sessionPayloadFactory.buildForRecipient(recipient, sessionToken);
        }
        throw new IllegalStateException("Tipo utente non supportato per la sessione self-care: " + user.getClass());
    }
}
