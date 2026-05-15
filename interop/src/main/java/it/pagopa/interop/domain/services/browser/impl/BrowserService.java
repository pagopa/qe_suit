package it.pagopa.interop.domain.services.browser.impl;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.domain.services.browser.WebBrowserService;
import it.pagopa.interop.domain.web.commons.component.Snackbar;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
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

    @Override
    public String getSnackbarErrorMessage(){
        Snackbar snackbar = webPresentationGateway.bind(Snackbar.class);

        if(!snackbar.alert().isError())
            throw new IllegalStateException("Snackbar non in stato di errore, impossibile leggere il messaggio");

        return snackbar.alert().message().read();
    }
}