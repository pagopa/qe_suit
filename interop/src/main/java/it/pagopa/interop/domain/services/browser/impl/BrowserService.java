package it.pagopa.interop.domain.services.browser.impl;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.domain.services.browser.WebBrowserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BrowserService implements WebBrowserService {

    private final WebPresentationGateway webPresentationGateway;

    @Override
    public boolean hasSessionToken() {
        return webPresentationGateway.getLocalStorageItem("token").isPresent();
    }

    @Override
    public void setSessionToken(String token) {
        webPresentationGateway.setLocalStorageItem("token", token);
    }
}
