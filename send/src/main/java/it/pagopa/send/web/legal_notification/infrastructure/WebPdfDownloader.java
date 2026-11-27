package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.send.web.infrastructure.WebBrowserLocation;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

/**
 * Scarica un documento dal portale della PA, che lo apre come PDF nella scheda del browser: il documento è scaricato
 * quando il browser lascia il portale per un PDF. Finché non succede (il documento non è ancora disponibile o il
 * portale avvisa di riprovare) si ripete il click.
 */
@Component
@RequiredArgsConstructor
public class WebPdfDownloader {

    private static final Duration DOWNLOAD_TIMEOUT = Duration.ofMinutes(2);
    private static final Duration DOWNLOAD_INTERVAL = Duration.ofSeconds(5);
    private static final String PDF_EXTENSION = ".pdf";

    private final WebBrowserLocation browserLocation;

    /**
     * @param openAndClick apre la pagina del documento e ne clicca il link
     * @param document     il documento, per il messaggio d'errore
     * @return il nome del file PDF aperto
     */
    public String download(Runnable openAndClick, String document) {
        // un PDF già aperto da un download precedente non conta
        String startUrl = browserLocation.currentUrl();
        return PollingUtils.pollUntil(
                        () -> openedPdf(startUrl).or(() -> {
                            openAndClick.run();
                            return Optional.empty();
                        }),
                        Optional::isPresent,
                        DOWNLOAD_TIMEOUT,
                        DOWNLOAD_INTERVAL,
                        document + " non si apre come PDF dal portale della PA")
                .orElseThrow();
    }

    private Optional<String> openedPdf(String startUrl) {
        String url = browserLocation.currentUrl();
        if (url.equals(startUrl)) {
            return Optional.empty();
        }
        String path = URI.create(url).getPath();
        String fileName = path == null ? "" : String.valueOf(Path.of(path).getFileName());
        return fileName.toLowerCase().endsWith(PDF_EXTENSION) ? Optional.of(fileName) : Optional.empty();
    }
}
