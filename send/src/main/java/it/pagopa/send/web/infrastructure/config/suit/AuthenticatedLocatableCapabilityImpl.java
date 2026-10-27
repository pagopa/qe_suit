package it.pagopa.send.web.infrastructure.config.suit;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.impl.LocatableCapabilityImpl;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.web.login.infrastructure.DynamicUserTokenProvider;
import it.pagopa.send.web.login.infrastructure.SelfCareSessionPayloadFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedLocatableCapabilityImpl extends LocatableCapabilityImpl {

    private static final String SESSION_STORAGE_KEY = "user";

    private final IWebPresentationApiAdapter adapter;
    private final CurrentUserSession currentUserSession;
    private final BrowserContext browserContext;
    private final SelfCareSessionPayloadFactory sessionPayloadFactory;
    private final DynamicUserTokenProvider tokenProvider;

    public AuthenticatedLocatableCapabilityImpl(
            IWebPresentationApiAdapter adapter,
            CurrentUserSession currentUserSession,
            BrowserContext browserContext,
            SelfCareSessionPayloadFactory sessionPayloadFactory,
            DynamicUserTokenProvider tokenProvider
    ) {
        super(adapter);
        this.adapter = adapter;
        this.currentUserSession = currentUserSession;
        this.browserContext = browserContext;
        this.sessionPayloadFactory = sessionPayloadFactory;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void navigateTo(String... pathParams) {
        Url targetUrl = resolveUrl(pathParams, urlSupplier.get().getUrl());

        // Nessun utente noto: pagine del flusso SPID reale (OneIdPage, PfLoginPage, ...),
        // navigazione semplice senza toccare il sessionStorage.
        if (!currentUserSession.hasUser()) {
            adapter.navigateTo(targetUrl);
            browserContext.setCurrentUrl(targetUrl);
            return;
        }

        User currentUser = currentUserSession.getUser();
        String sessionToken = sessionTokenFor(currentUser);
        String sessionPayload = buildSessionPayload(currentUser, sessionToken);

        // Il sessionStorage è legato all'origin corrente. Se il browser non è ancora atterrato
        // sul portale, il payload verrebbe scritto sull'origin sbagliato.
        if (browserContext.getCurrentUrl() == null) {
            adapter.navigateTo(targetUrl);
        }

        adapter.setSessionStorageItem("lang", "it");
        adapter.setSessionStorageItem(SESSION_STORAGE_KEY, sessionPayload);

        adapter.navigateTo(targetUrl);
        browserContext.setCurrentUrl(targetUrl);
    }

    private String sessionTokenFor(User user) {
        return tokenProvider.requireToken(user.getUsername());
    }

    private String buildSessionPayload(User user, String sessionToken) {
        return sessionPayloadFactory.build(user, sessionToken);
    }
}