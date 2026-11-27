package it.pagopa.send.suite.contract.radd;

import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.contract.http.TargetExpression;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.radd.domain.RaddOperationType;
import it.pagopa.send.common.radd.domain.RaddTransaction;
import it.pagopa.send.common.radd.domain.RaddUpload;
import it.pagopa.send.generated.openapi.clients.radd.ApiClient;
import it.pagopa.send.generated.openapi.clients.radd.model.AbortTransactionRequest;
import it.pagopa.send.generated.openapi.clients.radd.model.ActStartTransactionRequest;
import it.pagopa.send.generated.openapi.clients.radd.model.AorStartTransactionRequest;
import it.pagopa.send.generated.openapi.clients.radd.model.CompleteTransactionRequest;
import it.pagopa.send.radd.infrastructure.RaddActNotificationPool;
import it.pagopa.send.radd.infrastructure.RaddDocumentsZipFactory;
import it.pagopa.send.radd.infrastructure.RaddRequestFactory;
import it.pagopa.send.radd.infrastructure.config.RaddAltApiContractConfig;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static it.pagopa.send.radd.infrastructure.config.RaddAltApiContractConfig.SUCCESS_STATUS_CODE;
import static it.pagopa.send.radd.infrastructure.config.RaddAltApiContractConfig.businessError;
import static it.pagopa.send.radd.infrastructure.config.RaddAltApiContractConfig.status;

/**
 * Contract test (fuzzing) delle API B2B RADD ({@code pn-radd-alt-external.yaml}), con il JWT statico dell'operatore
 * ({@code radd.jwt.raddista-1}; l'header {@code uid} ha un default sovrascrivibile con {@code radd.api.uid}).
 * <p>
 * Checklist delle operazioni coperte (path sotto {@code /radd-net/api/v1}):
 * <ol>
 *   <li>{@code documentUpload}         POST /documents/upload                          payload     200</li>
 *   <li>{@code actInquiry}             GET  /act/inquiry                               queryParams 200</li>
 *   <li>{@code aorInquiry}             GET  /aor/inquiry                               queryParams 200</li>
 *   <li>{@code startActTransaction}    POST /act/transaction/start                     payload     200</li>
 *   <li>{@code completeActTransaction} POST /act/transaction/complete                  payload     200</li>
 *   <li>{@code abortActTransaction}    POST /act/transaction/abort                     payload     200</li>
 *   <li>{@code startAorTransaction}    POST /aor/transaction/start                     payload     200</li>
 *   <li>{@code completeAorTransaction} POST /aor/transaction/complete                  payload     200</li>
 *   <li>{@code abortAorTransaction}    POST /aor/transaction/abort                     payload     200</li>
 *   <li>{@code documentDownload}       GET  /download/{operationType}/{operationId}    pathParams  200 (pdf)</li>
 * </ol>
 * La validazione del contenuto del file caricato e' in {@link RaddAltDocumentContentContractTest}. Fuori scope: la
 * PUT del file sulla presigned url (non e' un'operazione dello spec) e l'header {@code uid} (non fuzzabile).
 * <p>
 * Precondizioni: la notifica ACT ({@code COMUNE_1} -> {@code USER_1}) e' condivisa via
 * {@link RaddActNotificationPool}; ogni caso che referenzia una transazione ACT ne avvia una nuova (upload + start).
 * {@code USER_1} non ha AAR non recapitati: la start AOR risponde con un esito di business diverso da 0 e
 * complete/abort AOR lavorano su una transazione inesistente (HTTP 200, code 1 da spec), il che basta a
 * verificarne il contratto HTTP. La classe e' serializzata con {@link ResourceLock} per il throttling
 * {@code radd-by-cf} sul codice fiscale del destinatario ({@code @Execution} e' vietata da
 * {@code ArchitectureRulesTest}).
 * <p>
 * Aspettative derivate dallo spec, da confermare al primo run annotando qui gli scostamenti.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, RaddAltApiContractConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ResourceLock(RaddAltContractTest.RADD_RECIPIENT_LOCK)
public class RaddAltContractTest {

    static final String RADD_RECIPIENT_LOCK = "pn-radd-alt-recipient";

    private static final List<FuzzScenario> NULL_OR_REMOVED =
            List.of(FuzzScenario.REPLACED_WITH_NULL, FuzzScenario.REMOVED);
    /**
     * {@code operationId} e' una stringa libera (niente format uuid, quindi niente scenari MALFORMED/NIL uuid): un
     * identificativo ben formato ma sconosciuto e' una transazione inesistente, HTTP 200 con code 1 da spec.
     */
    private static final FuzzScenario UNKNOWN_TRANSACTION = FuzzScenario.REPLACED_WITH_URL_INJECTION;

    /** Opzionali da spec: il file zip e' facoltativo e {@code operationDate} non e' required. */
    private static final List<TargetExpression<ActStartTransactionRequest>> ACT_START_OPTIONAL_FIELDS = List.of(
            ActStartTransactionRequest::getVersionToken,
            ActStartTransactionRequest::getFileKey,
            ActStartTransactionRequest::getChecksum,
            ActStartTransactionRequest::getOperationDate
    );
    private static final List<TargetExpression<AorStartTransactionRequest>> AOR_START_OPTIONAL_FIELDS = List.of(
            AorStartTransactionRequest::getVersionToken,
            AorStartTransactionRequest::getFileKey,
            AorStartTransactionRequest::getChecksum,
            AorStartTransactionRequest::getOperationDate
    );

    private final ApiClient apiClient;
    private final HttpContractValidator httpContractValidator;
    private final HttpContractValidator downloadHttpContractValidator;
    private final SendJourney sendJourney;
    private final RaddRequestFactory requestFactory;
    private final RaddDocumentsZipFactory zipFactory;
    private final RaddActNotificationPool actNotification;

    public RaddAltContractTest(
            @Qualifier("raddApiClient") ApiClient apiClient,
            @Qualifier("raddHttpContractValidator") HttpContractValidator httpContractValidator,
            @Qualifier("raddDownloadHttpContractValidator") HttpContractValidator downloadHttpContractValidator,
            SendJourney sendJourney,
            RaddRequestFactory requestFactory,
            RaddDocumentsZipFactory zipFactory,
            RaddActNotificationPool actNotification
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.downloadHttpContractValidator = downloadHttpContractValidator;
        this.sendJourney = sendJourney;
        this.requestFactory = requestFactory;
        this.zipFactory = zipFactory;
        this.actNotification = actNotification;
    }

    @TestFactory
    Stream<DynamicTest> documentUpload() {
        return httpContractValidator
                .apiCall(() -> apiClient.documentOperations().documentUpload())
                .payload(() -> {
                    String operationId = UUID.randomUUID().toString();
                    return requestFactory.documentUploadRequest(operationId, zipFactory.validZip(operationId).checksum());
                })
                .tests();
    }

    /** {@code iun} e' l'alternativa al {@code qrCode} (assente): senza nessuno dei due l'input non e' valido. */
    @TestFactory
    Stream<DynamicTest> actInquiry() {
        return httpContractValidator
                .apiCall(() -> apiClient.actOperations().actInquiry())
                .queryParams(() -> requestFactory.actInquiryQuery(actNotification.recipientTaxId(), actNotification.currentIun()))
                .targets(NULL_OR_REMOVED, status(400), queryParams("recipientTaxId", "recipientType", "iun"))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> aorInquiry() {
        return httpContractValidator
                .apiCall(() -> apiClient.aorOperations().aorInquiry())
                .queryParams(() -> requestFactory.aorInquiryQuery(actNotification.recipientTaxId()))
                .targets(NULL_OR_REMOVED, status(400), queryParams("recipientTaxId", "recipientType"))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> startActTransaction() {
        return httpContractValidator
                .apiCall(() -> apiClient.actOperations().startActTransaction())
                .payload(() -> requestFactory.actStartRequest(
                        uploadedDocuments(), actNotification.currentIun(), actNotification.recipientTaxId()))
                .targets(NULL_OR_REMOVED, expectSuccess(), ACT_START_OPTIONAL_FIELDS)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> completeActTransaction() {
        return httpContractValidator
                .apiCall(() -> apiClient.actOperations().completeActTransaction())
                .payload(() -> requestFactory.completeRequest(actNotification.startedActTransaction().operationId()))
                .targets(UNKNOWN_TRANSACTION, businessError(), List.of(CompleteTransactionRequest::getOperationId))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> abortActTransaction() {
        return httpContractValidator
                .apiCall(() -> apiClient.actOperations().abortActTransaction())
                .payload(() -> requestFactory.abortRequest(actNotification.startedActTransaction().operationId()))
                .targets(NULL_OR_REMOVED, expectSuccess(), List.of(AbortTransactionRequest::getReason))
                .targets(UNKNOWN_TRANSACTION, businessError(), List.of(AbortTransactionRequest::getOperationId))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> startAorTransaction() {
        return httpContractValidator
                .apiCall(() -> apiClient.aorOperations().startAorTransaction())
                .payload(() -> requestFactory.aorStartRequest(uploadedDocuments(), actNotification.recipientTaxId()))
                .targets(NULL_OR_REMOVED, expectSuccess(), AOR_START_OPTIONAL_FIELDS)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> completeAorTransaction() {
        return httpContractValidator
                .apiCall(() -> apiClient.aorOperations().completeAorTransaction())
                .payload(() -> requestFactory.completeRequest(aorTransaction().operationId()))
                .targets(UNKNOWN_TRANSACTION, businessError(), List.of(CompleteTransactionRequest::getOperationId))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> abortAorTransaction() {
        return httpContractValidator
                .apiCall(() -> apiClient.aorOperations().abortAorTransaction())
                .payload(() -> requestFactory.abortRequest(aorTransaction().operationId()))
                .targets(NULL_OR_REMOVED, expectSuccess(), List.of(AbortTransactionRequest::getReason))
                .targets(UNKNOWN_TRANSACTION, businessError(), List.of(AbortTransactionRequest::getOperationId))
                .tests();
    }

    /** Senza {@code attachmentId} il servizio restituisce il frontespizio della transazione. */
    @TestFactory
    Stream<DynamicTest> documentDownload() {
        return downloadHttpContractValidator
                .apiCall(() -> apiClient.documentOperations().documentDownload())
                .pathParams(this::actTransactionPathParams)
                .tests();
    }

    private Map<String, Object> actTransactionPathParams() {
        RaddTransaction transaction = actNotification.startedActTransaction();
        return Map.of(
                "operationType", RaddOperationType.ACT.pathValue(),
                "operationId", transaction.operationId()
        );
    }

    private RaddUpload uploadedDocuments() {
        return sendJourney.withUploadedDocuments().get(RaddUpload.class);
    }

    private RaddTransaction aorTransaction() {
        return sendJourney.startAorTransaction(actNotification.recipientTaxId()).get(RaddTransaction.class);
    }

    private static List<TargetExpression<Map<String, Object>>> queryParams(String... names) {
        return Stream.of(names)
                .<TargetExpression<Map<String, Object>>>map(name -> query -> query.get(name))
                .toList();
    }

    private static Consumer<Response> expectSuccess() {
        return status(SUCCESS_STATUS_CODE);
    }
}
