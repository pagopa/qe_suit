package it.pagopa.interop.common.infrastructure.context.cucumber;

import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.kernel.context.BrowserContext;
import lombok.Getter;
import lombok.Setter;

public class CucumberBrowserContext implements BrowserContext {
    @Getter
    @Setter
    private Url currentUrl;

    public void reset(){
        currentUrl = null;
    }
}
