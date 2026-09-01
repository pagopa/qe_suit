package it.pagopa.infrastructure.context;

import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.application.context.BrowserContext;

public class CucumberBrowserContext implements BrowserContext {

    private Url currentUrl;

    public void reset(){
        currentUrl = null;
    }

    @Override
    public Url getCurrentUrl() {
        return currentUrl;
    }

    @Override
    public void setCurrentUrl(Url currentUrl) {
        this.currentUrl = currentUrl;
    }
}
