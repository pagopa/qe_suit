package it.pagopa.infrastructure.browser;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.impl.LocatableCapabilityImpl;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.infrastructure.contract.browser.WebSessionProvider;

public class AuthenticatedLocatableCapability extends LocatableCapabilityImpl {
    private final BrowserContext browserContext;
    private final WebSessionProvider sessionProvider;

    public AuthenticatedLocatableCapability(
            IWebPresentationApiAdapter adapter,
            BrowserContext browserContext,
            WebSessionProvider sessionProvider
    ) {
        super(adapter);
        this.browserContext = browserContext;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public void navigateTo(String... pathParams) {
        sessionProvider.provide();

        Url targetUrl = resolveUrl(pathParams, urlSupplier.get().getUrl());

        adapter.navigateTo(targetUrl);
        browserContext.setCurrentUrl(targetUrl);
    }
}
