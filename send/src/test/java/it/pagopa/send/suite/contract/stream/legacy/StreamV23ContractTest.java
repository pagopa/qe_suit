package it.pagopa.send.suite.contract.stream.legacy;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.streamlegacy.ApiClient;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.StreamCreationRequestV23;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.StreamRequestV23;
import it.pagopa.send.stream.infrastructure.StreamLegacyCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamLegacyRequestFactory;
import it.pagopa.send.stream.infrastructure.config.StreamLegacyContractValidatorFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test (fuzzing) delle API B2B pn-stream v2.3 sulla configurazione degli stream
 * ({@code pn-stream-api-external-older-version.yaml}, tag Streams).
 * <p>
 * Checklist delle operazioni coperte:
 * <ol>
 *   <li>{@code createEventStreamV23} POST /delivery-progresses/v2.3/streams payload 200</li>
 *   <li>{@code listEventStreamsV23} GET /delivery-progresses/v2.3/streams baseline 200</li>
 *   <li>{@code retrieveEventStreamV23} GET /delivery-progresses/v2.3/streams/{streamId} pathParams 200</li>
 *   <li>{@code updateEventStreamV23} PUT /delivery-progresses/v2.3/streams/{streamId} payload+pathParams 200</li>
 *   <li>{@code removeEventStreamV23} DELETE /delivery-progresses/v2.3/streams/{streamId} pathParams 204</li>
 *   <li>{@code disableEventStreamV23} POST /delivery-progresses/v2.3/streams/{streamId}/action/disable pathParams 200</li>
 * </ol>
 * Gruppo di validazione B: {@code title}, {@code eventType}, {@code groups}, {@code filterValues}
 * (+ {@code replacedStreamId} in creazione, assente nella baseline).
 * Campi opzionali non {@code nullable} nello spec: assenti accettati, {@code null} esplicito rifiutato (400).
 * {@code title}: vuoto, di soli spazi, con SQL o URL e' valido per lo spec ({@code ^.*$}) ed e' accettato.
 * <p>
 * Ogni caso che richiede uno {@code streamId} usa uno stream nuovo creato con la stessa versione
 * ({@code withFreshLegacyStream}). Transizioni di stato non valide: {@link StreamLegacyStateTransitionContractTest}.
 * <p>
 * Le operazioni {@code v2.3} sono {@code deprecated} nello spec: testarle e' lo scopo della
 * classe, percio' il warning di deprecazione e' soppresso.
 */
@SuppressWarnings("deprecation")
public class StreamV23ContractTest extends AbstractStreamLegacyContractTest {

    private static final StreamApiVersion VERSION = StreamApiVersion.V23;

    public StreamV23ContractTest(
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
    Stream<DynamicTest> createEventStreamV23() {
        return validators.create(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().createEventStreamV23())
                .payload(() -> purgedCreationRequest(VERSION, StreamCreationRequestV23.class))
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), List.of(
                        StreamCreationRequestV23::getFilterValues))
                .targets(TITLE_ACCEPTED_SCENARIOS, expectTrackedSuccess(VERSION),
                        List.of(StreamCreationRequestV23::getTitle))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> listEventStreamsV23() {
        return validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().listEventStreamsV23())
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> retrieveEventStreamV23() {
        return withStreamIdExpectations(validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().retrieveEventStreamV23())
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEventStreamV23() {
        return withStreamIdExpectations(validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().updateEventStreamV23())
                .payload(() -> requestFactory.updateRequest(VERSION, StreamRequestV23.class))
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), List.of(
                        StreamRequestV23::getFilterValues))
                .targets(TITLE_ACCEPTED_SCENARIOS, expectSuccess(),
                        List.of(StreamRequestV23::getTitle))
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> removeEventStreamV23() {
        return withStreamIdExpectations(validators.remove(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().removeEventStreamV23())
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> disableEventStreamV23() {
        return withStreamIdExpectations(validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().disableEventStreamV23())
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }
}
