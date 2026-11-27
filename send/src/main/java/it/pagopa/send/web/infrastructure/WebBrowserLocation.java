package it.pagopa.send.web.infrastructure;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Indirizzo a cui si trova il browser dello scenario: serve quando un'azione sulla pagina porta il browser fuori dal
 * portale (es. l'apertura di un documento), dove nessun page object può leggere.
 */
@Component
@RequiredArgsConstructor
public class WebBrowserLocation {

    private final IWebPresentationApiAdapter adapter;

    public String currentUrl() {
        return adapter.getLocation().getUrl();
    }
}
