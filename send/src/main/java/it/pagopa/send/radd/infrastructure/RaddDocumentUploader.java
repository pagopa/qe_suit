package it.pagopa.send.radd.infrastructure;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.common.radd.domain.RaddUpload;
import it.pagopa.send.generated.openapi.clients.radd.ApiClient;
import it.pagopa.send.radd.infrastructure.RaddDocumentsZipFactory.RaddDocumentsZip;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Procedura di upload dello spec ("PROCEDURA DI UPLOAD"): {@code documentUpload} per ottenere la presigned url, poi
 * PUT del file ZIP su quella url. La PUT non e' un'operazione dello spec: va senza JWT, con gli header
 * {@code x-amz-meta-secret} e {@code x-amz-checksum-sha256}; il {@code versionToken} e' l'header
 * {@code x-amz-version-id} della risposta.
 */
@Component
public class RaddDocumentUploader {

    private static final String ZIP_CONTENT_TYPE = "application/zip";

    private final ApiClient apiClient;
    private final RaddRequestFactory requestFactory;

    public RaddDocumentUploader(@Qualifier("raddApiClient") ApiClient apiClient, RaddRequestFactory requestFactory) {
        this.apiClient = apiClient;
        this.requestFactory = requestFactory;
    }

    /**
     * Upload di {@code zip} dichiarandone il checksum corretto.
     */
    public RaddUpload upload(String operationId, RaddDocumentsZip zip) {
        Response response = apiClient.documentOperations().documentUpload()
                .body(requestFactory.documentUploadRequest(operationId, zip.checksum()))
                .execute(r -> r.then().statusCode(200).extract().response());
        int code = response.jsonPath().getInt("status.code");
        if (code != 0) {
            throw new IllegalStateException(
                    "documentUpload per " + operationId + " fallita con status.code " + code + ": " + response.asString());
        }
        String versionToken = put(
                response.jsonPath().getString("url"),
                response.jsonPath().getString("secret"),
                zip
        );
        return new RaddUpload(operationId, response.jsonPath().getString("fileKey"), zip.checksum(), versionToken);
    }

    private String put(String presignedUrl, String secret, RaddDocumentsZip zip) {
        Response response = RestAssured.given()
                .config(RestAssuredConfig.config().encoderConfig(
                        EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .urlEncodingEnabled(false)
                .filter(new HttpLoggingFilter())
                .contentType(ZIP_CONTENT_TYPE)
                .header("x-amz-meta-secret", secret)
                .header("x-amz-checksum-sha256", zip.checksum())
                .body(zip.content())
                .put(URI.create(presignedUrl));
        response.then().statusCode(200);
        String versionToken = response.header("x-amz-version-id");
        if (versionToken == null || versionToken.isBlank()) {
            throw new IllegalStateException("PUT dello ZIP RADD senza header x-amz-version-id nella risposta");
        }
        return versionToken;
    }
}
