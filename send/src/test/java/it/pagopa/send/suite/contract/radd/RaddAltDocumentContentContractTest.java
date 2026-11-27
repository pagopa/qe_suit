package it.pagopa.send.suite.contract.radd;

import io.restassured.response.Response;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.radd.domain.RaddUpload;
import it.pagopa.send.generated.openapi.clients.radd.ApiClient;
import it.pagopa.send.generated.openapi.clients.radd.model.ActStartTransactionRequest;
import it.pagopa.send.radd.infrastructure.RaddActNotificationPool;
import it.pagopa.send.radd.infrastructure.RaddDocumentUploader;
import it.pagopa.send.radd.infrastructure.RaddDocumentsZipFactory;
import it.pagopa.send.radd.infrastructure.RaddDocumentsZipFactory.RaddDocumentsZip;
import it.pagopa.send.radd.infrastructure.RaddRequestFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Validazione del file caricato con la procedura di upload, verificata sulla {@code startActTransaction}: e' li' che
 * RADD confronta {@code fileKey}, {@code checksum} e {@code versionToken} con il file realmente caricato.
 * <p>
 * Il motore di fuzzing muta solo i campi del DTO, non il contenuto binario, e qui la richiesta di partenza e'
 * volutamente non valida: per questo sono {@link DynamicTest} espliciti e non passano da {@code HttpContractValidator}
 * (che pretende un caso baseline di successo). Lo spec non dichiara limiti di dimensione: si verificano solo formato
 * e coerenza.
 * <p>
 * Esito atteso: rifiuto, cioe' HTTP 400 oppure HTTP 200 con {@code status.code} diverso da 0 (RADD restituisce gli
 * errori di input anche come code 10). Da confermare al primo run.
 */
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ResourceLock(RaddAltContractTest.RADD_RECIPIENT_LOCK)
public class RaddAltDocumentContentContractTest {

    private final ApiClient apiClient;
    private final RaddRequestFactory requestFactory;
    private final RaddDocumentsZipFactory zipFactory;
    private final RaddDocumentUploader uploader;
    private final RaddActNotificationPool actNotification;

    public RaddAltDocumentContentContractTest(
            @Qualifier("raddApiClient") ApiClient apiClient,
            RaddRequestFactory requestFactory,
            RaddDocumentsZipFactory zipFactory,
            RaddDocumentUploader uploader,
            RaddActNotificationPool actNotification
    ) {
        this.apiClient = apiClient;
        this.requestFactory = requestFactory;
        this.zipFactory = zipFactory;
        this.uploader = uploader;
        this.actNotification = actNotification;
    }

    @TestFactory
    Stream<DynamicTest> startActTransactionRejectsInconsistentDocuments() {
        return Stream.of(
                dynamicTest("checksum diverso da quello del file caricato", () -> assertRejected(
                        startAct(zipFactory::validZip, request -> request.checksum(otherChecksum())))),
                dynamicTest("versionToken non corrispondente alla PUT", () -> assertRejected(
                        startAct(zipFactory::validZip, request -> request.versionToken("QE-INVALID-VERSION-TOKEN")))),
                dynamicTest("fileKey inesistente", () -> assertRejected(
                        startAct(zipFactory::validZip, request -> request.fileKey(unknownFileKey())))),
                dynamicTest("ZIP senza metadati_destinatario.json", () -> assertRejected(
                        startAct(operationId -> zipFactory.zipWithoutRecipientMetadata(), UnaryOperator.identity()))),
                dynamicTest("file non ZIP", () -> assertRejected(
                        startAct(operationId -> notAZip(), UnaryOperator.identity())))
        );
    }

    /**
     * Upload del file prodotto da {@code zip} con un {@code operationId} nuovo, poi start ACT sulla notifica condivisa
     * con la richiesta alterata da {@code tamper}.
     */
    private Response startAct(Function<String, RaddDocumentsZip> zip, UnaryOperator<ActStartTransactionRequest> tamper) {
        String operationId = UUID.randomUUID().toString();
        RaddUpload upload = uploader.upload(operationId, zip.apply(operationId));
        ActStartTransactionRequest request = requestFactory.actStartRequest(
                upload, actNotification.currentIun(), actNotification.recipientTaxId());
        return apiClient.actOperations().startActTransaction()
                .body(tamper.apply(request))
                .execute(Function.identity());
    }

    private static void assertRejected(Response response) {
        if (response.statusCode() == 200) {
            assertThat(response.jsonPath().getInt("status.code"))
                    .as("status.code di un file non valido: %s", response.asString())
                    .isNotZero();
        } else {
            assertThat(response.statusCode())
                    .as("HTTP status di un file non valido: %s", response.asString())
                    .isEqualTo(400);
        }
    }

    private RaddDocumentsZip notAZip() {
        byte[] content = "questo file non e' uno zip".getBytes(StandardCharsets.UTF_8);
        return new RaddDocumentsZip(content, zipFactory.sha256Base64(content));
    }

    private String otherChecksum() {
        return zipFactory.sha256Base64("contenuto diverso".getBytes(StandardCharsets.UTF_8));
    }

    private static String unknownFileKey() {
        return "PN_RADD_ALT_ATTACHMENT-" + UUID.randomUUID().toString().replace("-", "") + ".zip";
    }
}
