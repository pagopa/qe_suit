package it.pagopa.interop.common.kernel.context;

import it.frontend.e2e.framework.web.model.location.Url;

public interface BrowserContext {
    Url getCurrentUrl();
    void setCurrentUrl(Url url);
    void reset();
}
