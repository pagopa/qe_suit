package it.pagopa.send.suite.contract.stream.legacy;

import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.HttpContractStages.PathParamsStage;
import it.pagopa.infrastructure.contract.http.HttpContractStages.QueryParamsStage;
import it.pagopa.infrastructure.contract.http.TargetExpression;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.stream.domain.LegacyEventStream;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.streamlegacy.ApiClient;
import it.pagopa.send.stream.infrastructure.StreamLegacyCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamLegacyRequestFactory;
import it.pagopa.send.stream.infrastructure.StreamLegacyStatusCodes;
import it.pagopa.send.stream.infrastructure.config.StreamLegacyContractValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Base comune dei contract test sulle API B2B pn-stream legacy ({@code pn-stream-api-external-older-version.yaml}):
 * contesto Spring, utenza, precondizioni e aspettative condivise dalle classi per versione ed entita'.
 * <p>
 * Lo spec ammette un numero limitato di stream attivi per PA, condiviso con i test pn-stream v3.0
 * (QA-19146): stesso {@link ResourceLock} ({@code @Execution} e' vietata sui ContractTest da
 * {@code ArchitectureRulesTest}). Prima di ogni creazione e a fine classe si rimuovono solo gli stream
 * creati da questa esecuzione; i residui di esecuzioni precedenti, riconosciuti dal titolo, solo
 * all'avvio della classe, per non interferire con un'esecuzione parallela sulla stessa PA.
 * <p>
 * Aspettative derivate dallo spec e dal codice di pn-stream (lambda di versioning, StreamsServiceImpl),
 * da confermare al primo run annotando gli scostamenti nella classe interessata.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ResourceLock("pn-stream-active-streams")
abstract class AbstractStreamLegacyContractTest {

    protected static final UserAlias OWNER = UserAlias.COMUNE_1;

    /**
     * {@code title} accetta qualsiasi stringa fino a 256 caratteri ({@code ^.*$}, nessun {@code minLength}):
     * vuota, di soli spazi, con SQL o URL e' un titolo valido (verificato al primo run: 200).
     */
    protected static final List<FuzzScenario> TITLE_ACCEPTED_SCENARIOS = List.of(
            FuzzScenario.REPLACED_WITH_EMPTY_STRING,
            FuzzScenario.REPLACED_WITH_BLANK_STRING,
            FuzzScenario.REPLACED_WITH_SQL_INJECTION,
            FuzzScenario.REPLACED_WITH_URL_INJECTION
    );

    protected final ApiClient apiClient;
    protected final StreamLegacyContractValidatorFactory validators;
    protected final StreamLegacyRequestFactory requestFactory;
    protected final StreamLegacyCreatedTracker createdTracker;
    protected final SendJourney sendJourney;
    protected final UserProvider userProvider;

    protected AbstractStreamLegacyContractTest(
            ApiClient apiClient,
            StreamLegacyContractValidatorFactory validators,
            StreamLegacyRequestFactory requestFactory,
            StreamLegacyCreatedTracker createdTracker,
            SendJourney sendJourney,
            UserProvider userProvider
    ) {
        this.apiClient = apiClient;
        this.validators = validators;
        this.requestFactory = requestFactory;
        this.createdTracker = createdTracker;
        this.sendJourney = sendJourney;
        this.userProvider = userProvider;
    }

    @BeforeAll
    void removeLeftoversOfPreviousRuns() {
        sendJourney.removeLeftoverLegacyTestStreams(owner());
    }

    @AfterAll
    void removeCreatedStreams() {
        sendJourney.removeCreatedLegacyStreams(owner());
    }

    protected User owner() {
        return userProvider.resolve(OWNER);
    }

    /**
     * Richiesta baseline di creazione, dopo aver rimosso gli stream creati in precedenza da questa
     * esecuzione (limite di stream attivi).
     */
    protected <T> T purgedCreationRequest(StreamApiVersion version, Class<T> type) {
        sendJourney.removeCreatedLegacyStreams(owner());
        return requestFactory.creationRequest(version, type);
    }

    /**
     * Uno stream ATTIVO nuovo per ogni caso, creato con la stessa versione dell'operazione testata.
     */
    protected Map<String, Object> freshStreamPathParams(StreamApiVersion version) {
        LegacyEventStream stream = sendJourney.withFreshLegacyStream(owner(), version).get(LegacyEventStream.class);
        return Map.of("streamId", stream.getId());
    }

    /**
     * Baseline di {@code consumeEventStream}: cursore sintatticamente valido, cosi' che anche
     * {@code lastEventId} venga sottoposto al fuzzing.
     */
    protected static Map<String, Object> lastEventIdQueryParams() {
        return Map.of("lastEventId", StreamLegacyRequestFactory.BASELINE_LAST_EVENT_ID);
    }

    /**
     * Aspettative dedicate sullo {@code streamId} (format uuid) delle operazioni che lo referenziano:
     * UUID malformato rifiutato in validazione, UUID nil valido ma inesistente (404, 400 in v1.0).
     */
    protected static PathParamsStage<Map<String, Object>> withStreamIdExpectations(
            PathParamsStage<Map<String, Object>> stage, StreamApiVersion version) {
        List<TargetExpression<Map<String, Object>>> streamId = List.of(params -> params.get("streamId"));
        return stage
                .targets(FuzzScenario.REPLACED_WITH_MALFORMED_UUID, expectValidationError(), streamId)
                .targets(FuzzScenario.REPLACED_WITH_NIL_UUID, expectStatus(StreamLegacyStatusCodes.notFound(version)), streamId);
    }

    /**
     * Aspettative dedicate su {@code lastEventId} (query, opzionale, {@code ^0\d{37}$}), osservate al primo run:
     * <ul>
     *   <li>parametro assente (intera query rimossa o nulla, o solo il cursore rimosso): 200, si legge dal primo evento;</li>
     *   <li>parametro senza valore ({@code null}): 400;</li>
     *   <li>stringa vuota: 200, trattata come assente (scostamento dal pattern dello spec);</li>
     *   <li>stringa di 5000 caratteri: 403, bloccata dal WAF prima della validazione.</li>
     * </ul>
     */
    protected static QueryParamsStage<Map<String, Object>> withLastEventIdExpectations(
            QueryParamsStage<Map<String, Object>> stage) {
        List<TargetExpression<Map<String, Object>>> lastEventId = List.of(query -> query.get("lastEventId"));
        return stage
                .scenario(List.of(FuzzScenario.REPLACED_WITH_NULL, FuzzScenario.REMOVED), expectSuccess())
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), lastEventId)
                .targets(FuzzScenario.REPLACED_WITH_EMPTY_STRING, expectSuccess(), lastEventId)
                .targets(FuzzScenario.REPLACED_WITH_LONG_STRING, expectStatus(StreamLegacyStatusCodes.WAF_BLOCKED), lastEventId);
    }

    /**
     * Campi opzionali ma non {@code nullable} nello spec ({@code filterValues}, {@code waitForAccepted}): il
     * gateway rifiuta {@code null} esplicito (400), mentre l'assenza e' accettata. Va dichiarato, perche' il
     * framework deduce "null ammesso" dall'annotazione {@code @Nullable} del modello generato.
     */
    protected static Consumer<Response> expectValidationError() {
        return expectStatus(StreamLegacyStatusCodes.VALIDATION_ERROR);
    }

    protected static Consumer<Response> expectSuccess() {
        return expectStatus(StreamLegacyStatusCodes.SUCCESS);
    }

    /**
     * Successo atteso su una create: lo stream creato e' tracciato per la purge.
     */
    protected Consumer<Response> expectTrackedSuccess(StreamApiVersion version) {
        return createdTracker.tracking(version, expectSuccess());
    }

    protected static Consumer<Response> expectStatus(int status) {
        return response -> response.then().statusCode(status);
    }
}
