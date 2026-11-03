package it.pagopa.send.suite.contract;

import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.generated.openapi.clients.safestorage.ApiClient;
import it.pagopa.send.generated.openapi.clients.safestorage.model.FileCreationRequest;
import it.pagopa.send.generated.openapi.clients.safestorage.model.UpdateFileMetadataRequest;
import it.pagopa.send.safestorage.infrastructure.SafeStorageRequestFactory;
import it.pagopa.send.safestorage.infrastructure.SafeStorageUploadedFile;
import it.pagopa.send.safestorage.infrastructure.config.SafeStorageApiContractConfig;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, SafeStorageApiContractConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SafeStorageContractTest {

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;
    private final SafeStorageRequestFactory requestFactory;
    private final SafeStorageUploadedFile uploadedFile;

    public SafeStorageContractTest(
            ApiClient apiClient,
            @Qualifier("safeStorageHttpContractValidator") SendHttpContractValidator validator,
            SafeStorageRequestFactory requestFactory,
            SafeStorageUploadedFile uploadedFile
    ) {
        this.apiClient = apiClient;
        this.validator = validator;
        this.requestFactory = requestFactory;
        this.uploadedFile = uploadedFile;
    }

    /**
     * Un path o query parameter di 5000 caratteri supera il limite di lunghezza dell'URI del reverse
     * proxy, che risponde 414 prima che la richiesta raggiunga l'applicazione.
     */
    private static final int URI_TOO_LONG = 414;

    /**
     * Su una query string non esiste il valore null: il binder lo rende come testo letterale
     * {@code nome=null}, che il server rifiuta come valore non valido. Lo scenario coincide quindi
     * con l'invio di un valore errato, non con l'assenza del parametro (coperta da REMOVED).
     */
    private static final Consumer<Response> REJECTS_LITERAL_NULL = response -> response.then().statusCode(400);

    /**
     * Ogni query parameter di queste operazioni e' opzionale: toglierne uno, o toglierli tutti,
     * degenera nella chiamata baseline e deve avere successo.
     */
    private static final Consumer<Response> OPTIONAL_PARAM_DROPPED = response -> response.then().statusCode(200);

    @TestFactory
    Stream<DynamicTest> createFile() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.fileUpload().createFile()
                        .xPagopaSafestorageCxIdHeader(requestFactory.clientId())
                        .xChecksumValueHeader(requestFactory.checksumValue())
                        .xChecksumHeader("SHA256"))
                .payload(requestFactory::fileCreationRequest)
                // status e' in `required` nello spec, ma `nullable: true` fa emettere al generator
                // `required = false`: senza questo override l'inferenza si attende 200.
                .targets(
                        FuzzScenario.REMOVED,
                        response -> response.then().statusCode(400),
                        List.of(FileCreationRequest::getStatus)
                )
                // contentType non ha pattern ne' format nello spec (righe 741-744): e' una stringa
                // libera, quindi qualunque valore non vuoto e' conforme al contratto.
                .targets(
                        List.of(
                                FuzzScenario.REPLACED_WITH_LONG_STRING,
                                FuzzScenario.REPLACED_WITH_SQL_INJECTION,
                                FuzzScenario.REPLACED_WITH_URL_INJECTION,
                                FuzzScenario.REPLACED_WITH_XSS,
                                FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER
                        ),
                        response -> response.then().statusCode(200),
                        List.of(FileCreationRequest::getContentType)
                )
                // status ammette la stringa vuota e fino a 20 caratteri alfanumerici
                // (pattern "^$|^[A-Za-z0-9_]{0,20}$", riga 758): "" e "124" sono entrambi validi.
                .targets(
                        List.of(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER),
                        response -> response.then().statusCode(200),
                        List.of(FileCreationRequest::getStatus)
                )
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> getFile() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.fileDownload().getFile()
                        .xPagopaSafestorageCxIdHeader(requestFactory.clientId()))
                .pathParams(() -> Map.of("fileKey", uploadedFile.fileKey()))
                // La fileKey non ha un formato strutturato: un valore che rispetta il pattern ma non
                // esiste e' una risorsa assente, non una richiesta malformata.
                .scenario(
                        List.of(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER),
                        response -> response.then().statusCode(404)
                )
                .scenario(FuzzScenario.REPLACED_WITH_LONG_STRING, response -> response.then().statusCode(URI_TOO_LONG))
                .queryParams(() -> Map.of("metadataOnly", false, "tags", false))
                .scenario(FuzzScenario.REMOVED, OPTIONAL_PARAM_DROPPED)
                .scenario(FuzzScenario.REPLACED_WITH_NULL, REJECTS_LITERAL_NULL)
                // Alla radice REPLACED_WITH_NULL non e' un valore errato: e' l'intera query string
                // che sparisce, quindi vale la stessa regola di REMOVED.
                .targets(FuzzScenario.REPLACED_WITH_NULL, OPTIONAL_PARAM_DROPPED, List.of(query -> query))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateFileMetadata() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.fileMetadataUpdate().updateFileMetadata()
                        .xPagopaSafestorageCxIdHeader(requestFactory.clientId()))
                .pathParams(() -> Map.of("fileKey", uploadedFile.fileKey()))
                .scenario(
                        List.of(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER),
                        response -> response.then().statusCode(404)
                )
                .scenario(FuzzScenario.REPLACED_WITH_LONG_STRING, response -> response.then().statusCode(URI_TOO_LONG))
                .payload(requestFactory::updateFileMetadataRequest)
                // status ammette la stringa vuota: pattern "^[A-Za-z0-9_]{0,20}$" (riga 1015), min 0.
                .targets(
                        FuzzScenario.REPLACED_WITH_EMPTY_STRING,
                        response -> response.then().statusCode(200),
                        List.of(UpdateFileMetadataRequest::getStatus)
                )
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> additionalFileTagsGet() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.additionalFileTags().additionalFileTagsGet()
                        .xPagopaSafestorageCxIdHeader(requestFactory.clientId()))
                .pathParams(() -> Map.of("fileKey", uploadedFile.fileKey()))
                .scenario(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER, response -> response.then().statusCode(404))
                .scenario(FuzzScenario.REPLACED_WITH_LONG_STRING, response -> response.then().statusCode(URI_TOO_LONG))
                .tests();
    }

    @Disabled("""
            Bloccato da un difetto del modulo common, fuori dal perimetro di questa modifica.
            JacksonMutationValidityResolver.PropertyMeta.from (riga 144) costruisce i membri della
            proprieta' con List.of(accessor, field, getter), ma List.of rifiuta i null.
            Per AdditionalFileTagsUpdateRequest l'introspezione Jackson riporta field=null sulle
            proprieta' SET e DELETE (accessor e getter sono presenti), quindi ogni nodo diverso
            dalla radice solleva NullPointerException e la factory abortisce in pianificazione
            senza generare alcun caso. DTO con i tre membri valorizzati, come FileCreationRequest,
            non sono interessati: per questo createFile funziona.
            Fix necessario in common: sostituire List.of con Stream.of, che tollera i null prima
            del filtro Objects::nonNull gia' presente sulla riga.""")
    @TestFactory
    Stream<DynamicTest> additionalFileTagsUpdate() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.additionalFileTags().additionalFileTagsUpdate()
                        .xPagopaSafestorageCxIdHeader(requestFactory.clientId()))
                .pathParams(() -> Map.of("fileKey", uploadedFile.fileKey()))
                .payload(requestFactory::tagsUpdateRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> additionalFileTagsSearch() {
        return validator.as(UserAlias.COMUNE_1)
                // Il tag va aggiunto sulla reqSpec: il client generato espone tagParamsQuery(...),
                // che emette un parametro letteralmente chiamato "tagParams" anziche' la coppia
                // chiave-valore richiesta dallo stile form/explode dello spec.
                .apiCall(() -> apiClient.additionalFileTags().additionalFileTagsSearch()
                        .xPagopaSafestorageCxIdHeader(requestFactory.clientId())
                        .reqSpec(spec -> spec.addQueryParam("global_indexed_singlevalue", "test1")))
                .queryParams(() -> Map.of("logic", "and", "tags", false))
                .scenario(FuzzScenario.REMOVED, OPTIONAL_PARAM_DROPPED)
                .scenario(FuzzScenario.REPLACED_WITH_NULL, REJECTS_LITERAL_NULL)
                .scenario(FuzzScenario.REPLACED_WITH_LONG_STRING, response -> response.then().statusCode(URI_TOO_LONG))
                // Alla radice REPLACED_WITH_NULL coincide con REMOVED: resta solo il tagParams
                // aggiunto sulla reqSpec, che e' una ricerca valida.
                .targets(FuzzScenario.REPLACED_WITH_NULL, OPTIONAL_PARAM_DROPPED, List.of(query -> query))
                .tests();
    }

    @Disabled("""
            Bloccato dallo stesso difetto di common che disabilita additionalFileTagsUpdate:
            NullPointerException in JacksonMutationValidityResolver.PropertyMeta.from (riga 144),
            qui sulle proprieta' SET e DELETE annidate in Tags.""")
    @TestFactory
    Stream<DynamicTest> additionalFileTagsMassiveUpdate() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.additionalFileTags().additionalFileTagsMassiveUpdate()
                        .xPagopaSafestorageCxIdHeader(requestFactory.clientId()))
                .payload(() -> requestFactory.tagsMassiveUpdateRequest(uploadedFile.fileKey()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> getCurrentClientConfig() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.cfg().getCurrentClientConfig())
                .pathParams(() -> Map.of("clientId", requestFactory.clientId()))
                // Lo spec documenta 404 quando clientId differisce dal client autenticato (riga 259):
                // vale anche per un clientId sintatticamente valido ma inesistente.
                .scenario(
                        List.of(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER),
                        response -> response.then().statusCode(404)
                )
                .scenario(FuzzScenario.REPLACED_WITH_LONG_STRING, response -> response.then().statusCode(URI_TOO_LONG))
                .tests();
    }

}