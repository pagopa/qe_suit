package it.pagopa.interop.domain.services.browser.impl;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.domain.services.browser.WebBrowserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BrowserService implements WebBrowserService {

    private final WebPresentationGateway webPresentationGateway;

    @Value("${interop.web.base-url}")
    private String baseUrl;

    @Override
    public boolean hasSessionToken() {
        try {
            return webPresentationGateway.getLocalStorageItem("token").isPresent();
        } catch (RuntimeException ex) {
            // localStorage non accessibile (es. data:/about:blank)
            return false;
        }
    }

    @Override
    public void setSessionToken(String token) {
        try {
            webPresentationGateway.setLocalStorageItem("token", token);
        } catch (RuntimeException firstFailure) {
            // Fallback robusto: porta il browser su origin http(s), poi riprova
            webPresentationGateway.navigateTo(Url.of(baseUrl));
            webPresentationGateway.setLocalStorageItem("token", token);
        }
    }
}