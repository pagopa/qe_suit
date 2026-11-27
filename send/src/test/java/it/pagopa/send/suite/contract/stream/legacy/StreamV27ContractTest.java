package it.pagopa.send.suite.contract.stream.legacy;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.streamlegacy.ApiClient;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.StreamCreationRequestV27;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.StreamRequestV27;
import it.pagopa.send.stream.infrastructure.StreamLegacyApiOperations;
import it.pagopa.send.stream.infrastructure.StreamLegacyCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamLegacyRequestFactory;
import it.pagopa.send.stream.infrastructure.config.StreamLegacyContractValidatorFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test (fuzzing) delle API B2B pn-stream v2.7 sulla configurazione degli stream
 * ({@code pn-stream-api-external-older-version.yaml}, tag Streams).
 * <p>
 * Checklist delle operazioni coperte:
 * <ol>
 *   <li>{@code createEventStreamV27} POST /delivery-progresses/v2.7/streams payload 200</li>
 *   <li>{@code listEventStreamsV27} GET /delivery-progresses/v2.7/streams solo-baseline 200</li>
 *   <li>{@code retrieveEventStreamV27} GET /delivery-progresses/v2.7/streams/{streamId} pathParams 200</li>
 *   <li>{@code updateEventStreamV27} PUT /delivery-progresses/v2.7/streams/{streamId} payload+pathParams 200</li>
 *   <li>{@code removeEventStreamV27} DELETE /delivery-progresses/v2.7/streams/{streamId} pathParams 204</li>
 *   <li>{@code disableEventStreamV27} POST /delivery-progresses/v2.7/streams/{streamId}/action/disable pathParams 200</li>
 * </ol>
 * Gruppo di validazione C: {@code title}, {@code eventType}, {@code groups}, {@code filterValues},
 * {@code waitForAccepted} (+ {@code replacedStreamId} in creazione, assente nella baseline).
 * In update il backend ignora {@code waitForAccepted} (non modificabile): se assente la richiesta e' accettata.
 * Campi opzionali non {@code nullable} nello spec: assenti accettati, {@code null} esplicito rifiutato (400).
 * {@code title}: vuoto, di soli spazi, con SQL o URL e' valido per lo spec ({@code ^.*$}) ed e' accettato.
 * <p>
 * Ogni caso che richiede uno {@code streamId} usa uno stream nuovo creato con la stessa versione
 * ({@code withFreshLegacyStream}). Transizioni di stato non valide: {@link StreamLegacyStateTransitionContractTest}.
 * <p>
 * Le operazioni {@code v2.7} sono {@code deprecated} nello spec: testarle e' lo scopo della
 * classe, percio' il warning di deprecazione e' soppresso.
 */
@SuppressWarnings("deprecation")
public class StreamV27ContractTest extends AbstractStreamLegacyContractTest {

    private static final StreamApiVersion VERSION = StreamApiVersion.V27;

    public StreamV27ContractTest(
            ApiClient apiClient,
            StreamLegacyContractValidatorFactory validators,
            StreamLegacyRequestFactory requestFactory,
            StreamLegacyCreatedTracker createdTracker,
            SendJourney sendJourney,
            UserProvider userProvider,
            StreamLegacyApiOperations operations,
            CurrentUserSession currentUserSession
    ) {
        super(apiClient, validators, requestFactory, createdTracker, sendJourney, userProvider, operations,
                currentUserSession);
    }

    @TestFactory
    Stream<DynamicTest> createEventStreamV27() {
        return withBaseline(baseline(VERSION, ops -> ops.create().apply(purgedTestTitle(VERSION)), expectTrackedSuccess(VERSION)),
                validators.create(VERSION)
                        .as(OWNER)
                        .apiCall(() -> apiClient.streams().createEventStreamV27())
                        .payload(() -> purgedCreationRequest(VERSION, StreamCreationRequestV27.class))
                        .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), List.of(
                                StreamCreationRequestV27::getFilterValues,
                                StreamCreationRequestV27::getWaitForAccepted))
                        .targets(TITLE_ACCEPTED_SCENARIOS, expectTrackedSuccess(VERSION),
                                List.of(StreamCreationRequestV27::getTitle))
                        .tests());
    }

    /**
     * Operazione senza parametri: il fuzzing non genera casi, si verifica la risposta documentata.
     */
    @TestFactory
    Stream<DynamicTest> listEventStreamsV27() {
        return Stream.of(baseline(VERSION, ops -> ops.list().get(), expectSuccess()));
    }

    @TestFactory
    Stream<DynamicTest> retrieveEventStreamV27() {
        return withBaseline(baseline(VERSION, ops -> ops.retrieve().apply(freshStreamId(VERSION)), expectSuccess()),
                withStreamIdExpectations(validators.standard(VERSION)
                        .as(OWNER)
                        .apiCall(() -> apiClient.streams().retrieveEventStreamV27())
                        .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                        .tests());
    }

    @TestFactory
    Stream<DynamicTest> updateEventStreamV27() {
        return withBaseline(baseline(VERSION, ops -> ops.update().apply(freshStreamId(VERSION)), expectSuccess()),
                withStreamIdExpectations(validators.standard(VERSION)
                        .as(OWNER)
                        .apiCall(() -> apiClient.streams().updateEventStreamV27())
                        .payload(() -> requestFactory.updateRequest(VERSION, StreamRequestV27.class))
                        .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), List.of(
                                StreamRequestV27::getFilterValues,
                                StreamRequestV27::getWaitForAccepted))
                        .targets(TITLE_ACCEPTED_SCENARIOS, expectSuccess(),
                                List.of(StreamRequestV27::getTitle))
                        .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                        .tests());
    }

    @TestFactory
    Stream<DynamicTest> removeEventStreamV27() {
        return withBaseline(baseline(VERSION, ops -> ops.remove().apply(freshStreamId(VERSION)), expectRemoved()),
                withStreamIdExpectations(validators.remove(VERSION)
                        .as(OWNER)
                        .apiCall(() -> apiClient.streams().removeEventStreamV27())
                        .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                        .tests());
    }

    @TestFactory
    Stream<DynamicTest> disableEventStreamV27() {
        return withBaseline(baseline(VERSION, ops -> ops.disable().apply(freshStreamId(VERSION)), expectSuccess()),
                withStreamIdExpectations(validators.standard(VERSION)
                        .as(OWNER)
                        .apiCall(() -> apiClient.streams().disableEventStreamV27())
                        .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                        .tests());
    }
}
