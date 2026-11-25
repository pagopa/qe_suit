package it.pagopa.send.suite.contract.stream;

import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.HttpContractStages.PathParamsStage;
import it.pagopa.infrastructure.contract.http.TargetExpression;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.stream.domain.EventStream;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.stream.ApiClient;
import it.pagopa.send.generated.openapi.clients.stream.model.StreamCreationRequestV30;
import it.pagopa.send.generated.openapi.clients.stream.model.StreamRequestV30;
import it.pagopa.send.stream.infrastructure.StreamCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamRequestFactory;
import it.pagopa.send.stream.infrastructure.config.StreamApiContractConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static it.pagopa.send.stream.infrastructure.config.StreamApiContractConfig.DEFAULT_SUCCESS_STATUS_CODE;

/**
 * Contract test (fuzzing) delle API B2B pn-stream v3.0 ({@code pn-stream-api-external.yaml}).
 * <p>
 * Checklist delle operazioni coperte:
 * <ol>
 *   <li>{@code createEventStreamV30}   POST   /streams                            payload            200</li>
 *   <li>{@code listEventStreamsV30}    GET    /streams                            baseline           200</li>
 *   <li>{@code retrieveEventStreamV30} GET    /streams/{streamId}                 pathParams         200</li>
 *   <li>{@code updateEventStreamV30}   PUT    /streams/{streamId}                 payload+pathParams 200</li>
 *   <li>{@code removeEventStreamV30}   DELETE /streams/{streamId}                 pathParams         204</li>
 *   <li>{@code disableEventStreamV30}  POST   /streams/{streamId}/action/disable  pathParams         200</li>
 *   <li>{@code consumeEventStreamV30}  GET    /streams/{streamId}/events          pathParams         200</li>
 * </ol>
 * Fuori scope: {@code GET /status} (healthcheck) e il fuzz della query {@code lastEventId}.
 * <p>
 * Ogni caso che richiede uno {@code streamId} usa uno stream nuovo creato via
 * {@link SendJourney#withFreshStream(User)}. Lo spec ammette al massimo 10 stream attivi per PA:
 * il Journey rimuove gli stream di test prima di ogni creazione, la classe e' serializzata con
 * {@link ResourceLock} ({@code @Execution} e' vietata sui ContractTest da {@code ArchitectureRulesTest})
 * e a inizio/fine classe si rimuovono i residui.
 * <p>
 * Aspettative derivate dallo spec, da confermare al primo run annotando qui gli scostamenti.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, StreamApiContractConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ResourceLock("pn-stream-active-streams")
public class StreamContractTest {

    private static final UserAlias OWNER = UserAlias.COMUNE_1;

    private static final List<TargetExpression<StreamCreationRequestV30>> CREATION_NULLABLE_FIELDS = List.of(
            StreamCreationRequestV30::getFilterValues,
            StreamCreationRequestV30::getWaitForAccepted,
            StreamCreationRequestV30::getCommunicationType
    );

    /** {@code waitForAccepted} non e' modificabile in update: resta all'aspettativa di policy. */
    private static final List<TargetExpression<StreamRequestV30>> UPDATE_NULLABLE_FIELDS = List.of(
            StreamRequestV30::getFilterValues
    );

    private final ApiClient apiClient;
    private final SendHttpContractValidator httpContractValidator;
    private final SendHttpContractValidator createHttpContractValidator;
    private final SendHttpContractValidator removeHttpContractValidator;
    private final SendJourney sendJourney;
    private final UserProvider userProvider;
    private final StreamRequestFactory requestFactory;
    private final StreamCreatedTracker createdTracker;

    public StreamContractTest(
            @Qualifier("streamApiClient") ApiClient apiClient,
            @Qualifier("streamHttpContractValidator") SendHttpContractValidator httpContractValidator,
            @Qualifier("streamCreateHttpContractValidator") SendHttpContractValidator createHttpContractValidator,
            @Qualifier("streamRemoveHttpContractValidator") SendHttpContractValidator removeHttpContractValidator,
            SendJourney sendJourney,
            UserProvider userProvider,
            StreamRequestFactory requestFactory,
            StreamCreatedTracker createdTracker
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.createHttpContractValidator = createHttpContractValidator;
        this.removeHttpContractValidator = removeHttpContractValidator;
        this.sendJourney = sendJourney;
        this.userProvider = userProvider;
        this.requestFactory = requestFactory;
        this.createdTracker = createdTracker;
    }

    @BeforeAll
    void purgeLeftoversFromPreviousRuns() {
        sendJourney.purgeTestStreams(owner());
    }

    @AfterAll
    void purgeCreatedStreams() {
        sendJourney.purgeTestStreams(owner());
    }

    @TestFactory
    Stream<DynamicTest> createEventStream() {
        return createHttpContractValidator
                .as(OWNER)
                .apiCall(() -> apiClient.streams().createEventStreamV30())
                .payload(() -> {
                    sendJourney.purgeTestStreams(owner());
                    return requestFactory.creationRequest();
                })
                .targets(FuzzScenario.REPLACED_WITH_NULL, createdTracker.tracking(expectSuccess()), CREATION_NULLABLE_FIELDS)
                .targets(FuzzScenario.REPLACED_WITH_URL_INJECTION, createdTracker.tracking(expectSuccess()),
                        List.of(StreamCreationRequestV30::getTitle))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> listEventStreams() {
        return httpContractValidator
                .as(OWNER)
                .apiCall(() -> apiClient.streams().listEventStreamsV30())
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> retrieveEventStream() {
        return withStreamIdExpectations(httpContractValidator
                .as(OWNER)
                .apiCall(() -> apiClient.streams().retrieveEventStreamV30())
                .pathParams(this::freshStreamPathParams));
    }

    @TestFactory
    Stream<DynamicTest> updateEventStream() {
        return withStreamIdExpectations(httpContractValidator
                .as(OWNER)
                .apiCall(() -> apiClient.streams().updateEventStreamV30())
                .payload(requestFactory::updateRequest)
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectSuccess(), UPDATE_NULLABLE_FIELDS)
                .targets(FuzzScenario.REPLACED_WITH_URL_INJECTION, expectSuccess(), List.of(StreamRequestV30::getTitle))
                .pathParams(this::freshStreamPathParams));
    }

    @TestFactory
    Stream<DynamicTest> removeEventStream() {
        return withStreamIdExpectations(removeHttpContractValidator
                .as(OWNER)
                .apiCall(() -> apiClient.streams().removeEventStreamV30())
                .pathParams(this::freshStreamPathParams));
    }

    @TestFactory
    Stream<DynamicTest> disableEventStream() {
        return withStreamIdExpectations(httpContractValidator
                .as(OWNER)
                .apiCall(() -> apiClient.streams().disableEventStreamV30())
                .pathParams(this::freshStreamPathParams));
    }

    @TestFactory
    Stream<DynamicTest> consumeEventStream() {
        return withStreamIdExpectations(httpContractValidator
                .as(OWNER)
                .apiCall(() -> apiClient.events().consumeEventStreamV30())
                .pathParams(this::freshStreamPathParams));
    }

    private Map<String, Object> freshStreamPathParams() {
        EventStream stream = sendJourney.withFreshStream(owner()).get(EventStream.class);
        return Map.of("streamId", stream.getId());
    }

    private User owner() {
        return userProvider.resolve(OWNER);
    }

    /**
     * Aspettative dedicate sullo {@code streamId} (format uuid) delle operazioni che lo referenziano:
     * UUID malformato rifiutato in validazione, UUID nil valido ma inesistente.
     */
    private static Stream<DynamicTest> withStreamIdExpectations(PathParamsStage<Map<String, Object>> stage) {
        List<TargetExpression<Map<String, Object>>> streamId = List.of(params -> params.get("streamId"));
        return stage
                .targets(FuzzScenario.REPLACED_WITH_MALFORMED_UUID, expectValidationError(), streamId)
                .targets(FuzzScenario.REPLACED_WITH_NIL_UUID, expectNotFound(), streamId)
                .tests();
    }

    private static Consumer<Response> expectSuccess() {
        return response -> response.then().statusCode(DEFAULT_SUCCESS_STATUS_CODE);
    }

    private static Consumer<Response> expectValidationError() {
        return response -> response.then().statusCode(400);
    }

    /** Lo spec prevede 404 "Stream not found"; 403 se l'implementazione verifica prima l'ownership. */
    private static Consumer<Response> expectNotFound() {
        return response -> response.then().statusCode(404);
    }
}
