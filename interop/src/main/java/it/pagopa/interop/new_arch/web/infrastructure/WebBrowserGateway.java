package it.pagopa.interop.new_arch.web.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.new_arch.web.infrastructure.suit.component.Snackbar;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebBrowserGateway {
    private final WebPresentationGateway webPresentationGateway;

    @Value("${interop.web.base-url}")
    private String baseUrl;

    public boolean hasSessionToken() {
        try {
            return webPresentationGateway.getLocalStorageItem("token").isPresent();
        } catch (RuntimeException ex) {
            // localStorage non accessibile (es. data:/about:blank)
            return false;
        }
    }

    public void setSessionToken(String token) {
        try {
            webPresentationGateway.setLocalStorageItem("token", token);
        } catch (RuntimeException firstFailure) {
            // Fallback robusto: porta il browser su origin http(s), poi riprova
            webPresentationGateway.navigateTo(Url.of(baseUrl));
            webPresentationGateway.setLocalStorageItem("token", token);
        }
    }


    public boolean hasError() {
        Snackbar snackbar = webPresentationGateway.bind(Snackbar.class);
        return snackbar.alert().isError();
    }
}
