package it.pagopa.infrastructure.context;

import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.application.context.BrowserContext;

public class InMemoryBrowserContext implements BrowserContext {
    private final ThreadLocal<Url> currentUrl = new ThreadLocal<>();

    @Override
    public Url getCurrentUrl() {
        return currentUrl.get();
    }

    @Override
    public void setCurrentUrl(Url url) {
        currentUrl.set(url);
    }

    @Override
    public void reset() {
        currentUrl.remove();
    }
}
