package it.pagopa.send.suite.contract.stream.legacy;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.streamlegacy.ApiClient;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.StreamCreationRequest;
import it.pagopa.send.stream.infrastructure.StreamLegacyCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamLegacyRequestFactory;
import it.pagopa.send.stream.infrastructure.config.StreamLegacyContractValidatorFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test (fuzzing) delle API B2B pn-stream v1.0 sulla configurazione degli stream
 * ({@code pn-stream-api-external-older-version.yaml}, tag Streams).
 * <p>
 * Checklist delle operazioni coperte:
 * <ol>
 *   <li>{@code createEventStream} POST /delivery-progresses/streams payload 200</li>
 *   <li>{@code listEventStreams} GET /delivery-progresses/streams baseline 200</li>
 *   <li>{@code retrieveEventStream} GET /delivery-progresses/streams/{streamId} pathParams 200</li>
 *   <li>{@code updateEventStream} PUT /delivery-progresses/streams/{streamId} payload+pathParams 200</li>
 *   <li>{@code removeEventStream} DELETE /delivery-progresses/streams/{streamId} pathParams 204</li>
 * </ol>
 * Gruppo di validazione A: {@code title}, {@code eventType}, {@code filterValues}; l'update usa lo
 * stesso schema della creazione. Stream inesistente: 400 (la lambda di versioning converte 403/404).
 * Campi opzionali non {@code nullable} nello spec: assenti accettati, {@code null} esplicito rifiutato (400).
 * {@code title}: vuoto, di soli spazi, con SQL o URL e' valido per lo spec ({@code ^.*$}) ed e' accettato.
 * <p>
 * Ogni caso che richiede uno {@code streamId} usa uno stream nuovo creato con la stessa versione
 * ({@code withFreshLegacyStream}). Transizioni di stato non valide: {@link StreamLegacyStateTransitionContractTest}.
 * <p>
 * Le operazioni {@code v1.0} sono {@code deprecated} nello spec: testarle e' lo scopo della
 * classe, percio' il warning di deprecazione e' soppresso.
 */
@SuppressWarnings("deprecation")
public class StreamV10ContractTest extends AbstractStreamLegacyContractTest {

    private static final StreamApiVersion VERSION = StreamApiVersion.V10;

    public StreamV10ContractTest(
            ApiClient apiClient,
            StreamLegacyContractValidatorFactory validators,
            StreamLegacyRequestFactory requestFactory,
            StreamLegacyCreatedTracker createdTracker,
            SendJourney sendJourney,
            UserProvider userProvider
    ) {
        super(apiClient, validators, requestFactory, createdTracker, sendJourney, userProvider);
    }

    @TestFactory
    Stream<DynamicTest> createEventStream() {
        return validators.create(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().createEventStream())
                .payload(() -> purgedCreationRequest(VERSION, StreamCreationRequest.class))
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), List.of(
                        StreamCreationRequest::getFilterValues))
                .targets(TITLE_ACCEPTED_SCENARIOS, expectTrackedSuccess(VERSION),
                        List.of(StreamCreationRequest::getTitle))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> listEventStreams() {
        return validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().listEventStreams())
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> retrieveEventStream() {
        return withStreamIdExpectations(validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().retrieveEventStream())
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEventStream() {
        return withStreamIdExpectations(validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().updateEventStream())
                .payload(() -> requestFactory.updateRequest(VERSION, StreamCreationRequest.class))
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), List.of(
                        StreamCreationRequest::getFilterValues))
                .targets(TITLE_ACCEPTED_SCENARIOS, expectSuccess(),
                        List.of(StreamCreationRequest::getTitle))
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> removeEventStream() {
        return withStreamIdExpectations(validators.remove(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().removeEventStream())
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }
}
