package it.pagopa.send.safestorage.infrastructure;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.response.Response;
import it.pagopa.send.generated.openapi.clients.safestorage.ApiClient;
import it.pagopa.send.generated.openapi.clients.safestorage.model.FileCreationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Precondizione dei contract test SafeStorage: prenota un upload, carica il contenuto sul presigned
 * URL e tiene la fileKey risultante. Senza questo passaggio la chiamata non mutata risponderebbe
 * 404 e nessun esito sarebbe attribuibile alla mutazione.
 */
@Component
public class SafeStorageUploadedFile {

    private static final String CONTENT_TYPE = "application/pdf";

    private final ApiClient apiClient;
    private final SafeStorageRequestFactory requestFactory;
    private final AtomicReference<String> fileKey = new AtomicReference<>();

    public SafeStorageUploadedFile(
            @Qualifier("safeStorageApiClient") ApiClient apiClient,
            SafeStorageRequestFactory requestFactory
    ) {
        this.apiClient = apiClient;
        this.requestFactory = requestFactory;
    }

    /**
     * La fileKey di un file realmente caricato. Il caricamento avviene una sola volta per contesto
     * Spring: tutti i casi generati condividono lo stesso file, che e' immutabile per contratto.
     */
    public synchronized String fileKey() {
        // Not updateAndGet: that lambda may be retried under contention and would upload twice.
        String current = fileKey.get();
        if (current == null) {
            current = upload();
            fileKey.set(current);
        }
        return current;
    }

    private String upload() {
        byte[] content = content();
        String checksum = base64Sha256(content);

        FileCreationResponse created = apiClient.fileUpload().createFile()
                .xPagopaSafestorageCxIdHeader(requestFactory.clientId())
                .xChecksumValueHeader(checksum)
                .xChecksumHeader("SHA256")
                .body(requestFactory.uploadReservationRequest())
                .executeAs(response -> response.then().statusCode(200).extract().response());

        Response uploaded = RestAssured.given()
                // The presigned URL is already percent-encoded: re-encoding it would turn the "/"
                // inside X-Amz-Credential into %252F and S3 would reject the signature.
                .urlEncodingEnabled(false)
                // content-type is one of the signed headers, and it was signed as plain
                // "application/pdf": letting RestAssured append "; charset=..." breaks the signature.
                .config(RestAssured.config().encoderConfig(
                        EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)
                ))
                .contentType(CONTENT_TYPE)
                .header("x-amz-meta-secret", created.getSecret())
                .header("x-amz-checksum-sha256", checksum)
                .body(content)
                .put(created.getUploadUrl());

        if (uploaded.statusCode() != 200) {
            throw new IllegalStateException(
                    "SafeStorage presigned upload failed with status " + uploaded.statusCode()
                            + ": " + uploaded.asString()
            );
        }
        return created.getKey();
    }

    private byte[] content() {
        return "%PDF-1.4\n% contract test fixture\n%%EOF\n".getBytes(StandardCharsets.UTF_8);
    }

    private String base64Sha256(byte[] content) {
        try {
            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 not available", exception);
        }
    }
}
