package it.pagopa.send.service;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import it.pagopa.send.generated.openapi.clients.bff.api.NotificationSentApi;
import it.pagopa.send.generated.openapi.clients.bff.model.BffPreLoadRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffPreLoadResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.Problem;
import it.pagopa.send.model.PreloadedDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentPreloadService {

    private static final String TEST_PDF_CLASSPATH = "documents/test-attachment.pdf";

    private final NotificationSentApi notificationSentApi;

    /**
     * Esegue il flusso reale di preload SafeStorage per il PDF di test condiviso: calcola lo
     * sha256 in locale, chiama il preload BFF, poi carica davvero i byte sull'URL pre-firmato
     * restituito.
     */
    public PreloadedDocument preloadTestPdf(String preloadIdx) {
        byte[] fileBytes = readClasspathResource(TEST_PDF_CLASSPATH);
        String sha256Base64 = sha256Base64(fileBytes);

        BffPreLoadRequest preLoadRequest = new BffPreLoadRequest()
                .preloadIdx(preloadIdx)
                .contentType("application/pdf")
                .sha256(sha256Base64);

        List<BffPreLoadResponse> responses = executePreload(preLoadRequest);

        BffPreLoadResponse response = responses.stream()
                .filter(r -> preloadIdx.equals(r.getPreloadIdx()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nessuna risposta di preload per idx " + preloadIdx));

        uploadFile(response, fileBytes, sha256Base64);

        return new PreloadedDocument(response.getKey(), "v1", sha256Base64);
    }

    /**
     * Il body di successo (200) è un array di BffPreLoadResponse, ma in caso di errore (400, 401,
     * ecc.) l'API restituisce un singolo oggetto Problem: deserializzare sempre come lista, senza
     * prima controllare lo status code, produce un MismatchedInputException che nasconde l'errore
     * reale restituito dal BFF.
     */
    private List<BffPreLoadResponse> executePreload(BffPreLoadRequest preLoadRequest) {
        Response rawResponse = notificationSentApi.preSignedUploadV1()
                .body(List.of(preLoadRequest))
                .execute(Function.identity());

        if (rawResponse.getStatusCode() < 200 || rawResponse.getStatusCode() >= 300) {
            Problem problem = rawResponse.as(Problem.class);
            throw new IllegalStateException(String.format(
                    "Preload documento fallito con status %d: %s - %s",
                    rawResponse.getStatusCode(),
                    problem != null ? problem.getTitle() : null,
                    problem != null ? problem.getDetail() : rawResponse.asString()));
        }

        return rawResponse.as(new TypeRef<List<BffPreLoadResponse>>() {});
    }

    /**
     * Il preload (executePreload) non trasporta i byte del file: restituisce solo un URL
     * pre-firmato su SafeStorage/S3 a cui va fatto un upload diretto (bypassando il BFF), più i
     * metadati necessari a costruire quella richiesta:
     * - httpMethod/url: dove e con che verbo inviare i byte
     * - secret: prova che il chiamante è davvero autorizzato a scrivere su quell'URL (oltre a
     *   conoscerlo), va inviato come header
     * - lo sha256 dichiarato nel preload va ripetuto come header "x-amz-checksum-sha256": S3
     *   verifica che i byte caricati corrispondano davvero a quel checksum e rifiuta l'upload
     *   (400) se l'header manca o non corrisponde
     *
     * L'URL è pre-firmato (query string con firma AWS SigV4): rest-assured ricostruisce/normalizza
     * l'URI passato a .request(Method, url), corrompendo la codifica già presente nella query
     * string (es. "%2F" ridecodificato) e invalidando così la firma ("AuthorizationQueryParametersError").
     * Per questa singola chiamata, verso un URL esterno già completo e non verso l'API generata,
     * si usa quindi java.net.http.HttpClient, che tratta l'URL come stringa opaca (comportamento
     * analogo a URI.create(url) + RestTemplate usato in pn-b2b-client per lo stesso upload).
     */
    private void uploadFile(BffPreLoadResponse response, byte[] fileBytes, String sha256Base64) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(response.getUrl()))
                    .header("Content-Type", "application/pdf")
                    .header("x-amz-checksum-sha256", sha256Base64)
                    .header("x-amz-meta-secret", response.getSecret())
                    .method(response.getHttpMethod().name(), HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                    .build();

            HttpResponse<String> uploadResponse = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Upload su {} {} -> status {} body: {}",
                    response.getHttpMethod(), response.getUrl(), uploadResponse.statusCode(), uploadResponse.body());

            if (uploadResponse.statusCode() >= 300) {
                throw new IllegalStateException(String.format(
                        "Upload file fallito con status %d: %s",
                        uploadResponse.statusCode(), uploadResponse.body()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Upload file fallito", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Upload file interrotto", e);
        }
    }

    private byte[] readClasspathResource(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Impossibile leggere il PDF di test: " + path, e);
        }
    }

    private String sha256Base64(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
