package it.pagopa.send.radd.infrastructure;

import it.pagopa.send.common.radd.domain.RaddUpload;
import it.pagopa.send.generated.openapi.clients.radd.model.AbortTransactionRequest;
import it.pagopa.send.generated.openapi.clients.radd.model.ActStartTransactionRequest;
import it.pagopa.send.generated.openapi.clients.radd.model.AorStartTransactionRequest;
import it.pagopa.send.generated.openapi.clients.radd.model.CompleteTransactionRequest;
import it.pagopa.send.generated.openapi.clients.radd.model.DocumentUploadRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Richieste baseline delle operazioni RADD, valide per contratto: un fallimento del contract test deve essere sempre
 * attribuibile alla mutazione applicata dal fuzzer, non al fixture. Il destinatario e' sempre una persona fisica
 * senza delegato; le date sono RFC3339 in UTC.
 */
@Component
public class RaddRequestFactory {

    static final String RECIPIENT_TYPE_PF = "PF";
    static final String ABORT_REASON = "abort";

    public DocumentUploadRequest documentUploadRequest(String operationId, String checksum) {
        return new DocumentUploadRequest()
                .operationId(operationId)
                .checksum(checksum);
    }

    /**
     * Start ACT per IUN (il {@code qrCode} e' l'alternativa, qui assente) con il file caricato da {@code upload}.
     */
    public ActStartTransactionRequest actStartRequest(RaddUpload upload, String iun, String recipientTaxId) {
        return new ActStartTransactionRequest()
                .operationId(upload.operationId())
                .recipientTaxId(recipientTaxId)
                .recipientType(ActStartTransactionRequest.RecipientTypeEnum.PF)
                .iun(iun)
                .fileKey(upload.fileKey())
                .checksum(upload.checksum())
                .versionToken(upload.versionToken())
                .operationDate(now());
    }

    public AorStartTransactionRequest aorStartRequest(RaddUpload upload, String recipientTaxId) {
        return new AorStartTransactionRequest()
                .operationId(upload.operationId())
                .recipientTaxId(recipientTaxId)
                .recipientType(AorStartTransactionRequest.RecipientTypeEnum.PF)
                .fileKey(upload.fileKey())
                .checksum(upload.checksum())
                .versionToken(upload.versionToken())
                .operationDate(now());
    }

    public CompleteTransactionRequest completeRequest(String operationId) {
        return new CompleteTransactionRequest()
                .operationId(operationId)
                .operationDate(now());
    }

    public AbortTransactionRequest abortRequest(String operationId) {
        return new AbortTransactionRequest()
                .operationId(operationId)
                .reason(ABORT_REASON)
                .operationDate(now());
    }

    /**
     * Query di {@code actInquiry} per IUN; chiavi = nomi dei parametri OpenAPI.
     */
    public Map<String, Object> actInquiryQuery(String recipientTaxId, String iun) {
        Map<String, Object> query = aorInquiryQuery(recipientTaxId);
        query.put("iun", iun);
        return query;
    }

    public Map<String, Object> aorInquiryQuery(String recipientTaxId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("recipientTaxId", recipientTaxId);
        query.put("recipientType", RECIPIENT_TYPE_PF);
        return query;
    }

    private static String now() {
        return OffsetDateTime.now(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
