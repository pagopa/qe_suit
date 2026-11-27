package it.pagopa.send.common.radd.infrastructure;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import it.pagopa.send.common.radd.application.RaddGateway;
import it.pagopa.send.common.radd.domain.RaddOperationType;
import it.pagopa.send.common.radd.domain.RaddTransaction;
import it.pagopa.send.common.radd.domain.RaddUpload;
import it.pagopa.send.generated.openapi.clients.radd.ApiClient;
import it.pagopa.send.radd.infrastructure.RaddDocumentUploader;
import it.pagopa.send.radd.infrastructure.RaddDocumentsZipFactory;
import it.pagopa.send.radd.infrastructure.RaddRequestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Implementazione di {@link RaddGateway} sulle API B2B RADD, con il JWT statico dell'operatore configurato.
 * Lo {@code status.code} si legge dal JSON e non dal DTO generato: gli enum dei codici non coprono tutti i valori
 * che il servizio puo' restituire e la deserializzazione fallirebbe.
 */
@Component
public class RaddRestGateway implements RaddGateway {

    private final ApiClient apiClient;
    private final RaddRequestFactory requestFactory;
    private final RaddDocumentsZipFactory zipFactory;
    private final RaddDocumentUploader uploader;

    public RaddRestGateway(
            @Qualifier("raddApiClient") ApiClient apiClient,
            RaddRequestFactory requestFactory,
            RaddDocumentsZipFactory zipFactory,
            RaddDocumentUploader uploader
    ) {
        this.apiClient = apiClient;
        this.requestFactory = requestFactory;
        this.zipFactory = zipFactory;
        this.uploader = uploader;
    }

    @Override
    public RaddUpload uploadDocuments(String operationId) {
        return uploader.upload(operationId, zipFactory.validZip(operationId));
    }

    @Override
    public RaddTransaction startActTransaction(RaddUpload upload, String iun, String recipientTaxId) {
        Response response = apiClient.actOperations().startActTransaction()
                .body(requestFactory.actStartRequest(upload, iun, recipientTaxId))
                .execute(Function.identity());
        return toTransaction(upload.operationId(), RaddOperationType.ACT, response);
    }

    @Override
    public RaddTransaction startAorTransaction(RaddUpload upload, String recipientTaxId) {
        Response response = apiClient.aorOperations().startAorTransaction()
                .body(requestFactory.aorStartRequest(upload, recipientTaxId))
                .execute(Function.identity());
        return toTransaction(upload.operationId(), RaddOperationType.AOR, response);
    }

    private static RaddTransaction toTransaction(String operationId, RaddOperationType type, Response response) {
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Start transaction RADD " + type + " " + operationId
                    + " fallita con HTTP " + response.statusCode() + ": " + response.asString());
        }
        JsonPath body = response.jsonPath();
        Number retryAfter = body.get("status.retryAfter");
        return new RaddTransaction(
                operationId,
                type,
                body.getInt("status.code"),
                retryAfter == null ? null : retryAfter.longValue()
        );
    }
}
