package it.pagopa.send.bff.informal_notification.infrastructure;

import io.restassured.response.Response;
import it.pagopa.send.common.legal_notification.domain.PreloadedDocument;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformalDocumentPreloadService {

    private static final String TEST_PDF_CLASSPATH = "documents/test-attachment.pdf";

    private final InformalDeliveryRestClient informalDeliveryRestClient;
    private final CurrentUserSession currentUserSession;
    private final Map<String, String> paDeliveryApiKeys;

    @Value("${informal.delivery.api-key:68c854bc-a234-4f08-b738-d632814c84cc}")
    private String defaultApiKey;

    public PreloadedDocument preloadTestPdf(String preloadIdx) {
        byte[] fileBytes = readClasspathResource(TEST_PDF_CLASSPATH);
        String sha256Base64 = sha256Base64(fileBytes);

        Map<String, Object> preloadRequest = Map.of(
                "preloadIdx", preloadIdx,
                "contentType", "application/pdf",
                "sha256", sha256Base64
        );

        Tenant sender = currentUserSession.getSender();
        String apiKey = (sender != null && paDeliveryApiKeys != null)
                ? paDeliveryApiKeys.getOrDefault(sender.name(), defaultApiKey)
                : defaultApiKey;

        Response response = informalDeliveryRestClient.preloadAttachments(apiKey, List.of(preloadRequest));
        if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            throw new IllegalStateException(String.format(
                    "Preload allegato informale fallito con status %d: %s",
                    response.getStatusCode(), response.getBody().asString()));
        }

        List<Map<String, Object>> responses = response.jsonPath().getList("$");
        Map<String, Object> matched = responses.stream()
                .filter(r -> preloadIdx.equals(r.get("preloadIdx")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nessuna risposta di preload informale per idx " + preloadIdx));

        String url = (String) matched.get("url");
        String secret = (String) matched.get("secret");
        String httpMethod = (String) matched.getOrDefault("httpMethod", "PUT");
        String key = (String) matched.get("key");

        uploadFile(url, secret, httpMethod, fileBytes, sha256Base64);

        return new PreloadedDocument(key, "v1", sha256Base64);
    }

    private void uploadFile(String url, String secret, String httpMethod, byte[] fileBytes, String sha256Base64) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .header("Content-Type", "application/pdf")
                    .header("x-amz-checksum-sha256", sha256Base64)
                    .header("x-amz-meta-secret", secret)
                    .method(httpMethod, HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                    .build();

            HttpResponse<String> uploadResponse = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Upload informale su {} {} -> status {} body: {}",
                    httpMethod, url, uploadResponse.statusCode(), uploadResponse.body());

            if (uploadResponse.statusCode() >= 300) {
                throw new IllegalStateException(String.format(
                        "Upload file informale fallito con status %d: %s",
                        uploadResponse.statusCode(), uploadResponse.body()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Upload file informale fallito", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Upload file informale interrotto", e);
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
