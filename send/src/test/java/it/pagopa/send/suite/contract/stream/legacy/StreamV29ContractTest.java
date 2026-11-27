package it.pagopa.send.suite.contract.stream.legacy;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.streamlegacy.ApiClient;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.StreamCreationRequestV29;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.StreamRequestV29;
import it.pagopa.send.stream.infrastructure.StreamLegacyCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamLegacyRequestFactory;
import it.pagopa.send.stream.infrastructure.config.StreamLegacyContractValidatorFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test (fuzzing) delle API B2B pn-stream v2.9 sulla configurazione degli stream
 * ({@code pn-stream-api-external-older-version.yaml}, tag Streams).
 * <p>
 * Checklist delle operazioni coperte:
 * <ol>
 *   <li>{@code createEventStreamV29} POST /delivery-progresses/v2.9/streams payload 200</li>
 *   <li>{@code listEventStreamsV29} GET /delivery-progresses/v2.9/streams baseline 200</li>
 *   <li>{@code retrieveEventStreamV29} GET /delivery-progresses/v2.9/streams/{streamId} pathParams 200</li>
 *   <li>{@code updateEventStreamV29} PUT /delivery-progresses/v2.9/streams/{streamId} payload+pathParams 200</li>
 *   <li>{@code removeEventStreamV29} DELETE /delivery-progresses/v2.9/streams/{streamId} pathParams 204</li>
 *   <li>{@code disableEventStreamV29} POST /delivery-progresses/v2.9/streams/{streamId}/action/disable pathParams 200</li>
 * </ol>
 * Gruppo di validazione C: {@code title}, {@code eventType}, {@code groups}, {@code filterValues},
 * {@code waitForAccepted} (+ {@code replacedStreamId} in creazione, assente nella baseline).
 * In update il backend ignora {@code waitForAccepted} (non modificabile): se assente la richiesta e' accettata.
 * Campi opzionali non {@code nullable} nello spec: assenti accettati, {@code null} esplicito rifiutato (400).
 * {@code title}: vuoto, di soli spazi, con SQL o URL e' valido per lo spec ({@code ^.*$}) ed e' accettato.
 * <p>
 * Ogni caso che richiede uno {@code streamId} usa uno stream nuovo creato con la stessa versione
 * ({@code withFreshLegacyStream}). Transizioni di stato non valide: {@link StreamLegacyStateTransitionContractTest}.
 */
public class StreamV29ContractTest extends AbstractStreamLegacyContractTest {

    private static final StreamApiVersion VERSION = StreamApiVersion.V29;

    public StreamV29ContractTest(
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
    Stream<DynamicTest> createEventStreamV29() {
        return validators.create(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().createEventStreamV29())
                .payload(() -> purgedCreationRequest(VERSION, StreamCreationRequestV29.class))
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), List.of(
                        StreamCreationRequestV29::getFilterValues,
                        StreamCreationRequestV29::getWaitForAccepted))
                .targets(TITLE_ACCEPTED_SCENARIOS, expectTrackedSuccess(VERSION),
                        List.of(StreamCreationRequestV29::getTitle))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> listEventStreamsV29() {
        return validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().listEventStreamsV29())
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> retrieveEventStreamV29() {
        return withStreamIdExpectations(validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().retrieveEventStreamV29())
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEventStreamV29() {
        return withStreamIdExpectations(validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().updateEventStreamV29())
                .payload(() -> requestFactory.updateRequest(VERSION, StreamRequestV29.class))
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), List.of(
                        StreamRequestV29::getFilterValues,
                        StreamRequestV29::getWaitForAccepted))
                .targets(TITLE_ACCEPTED_SCENARIOS, expectSuccess(),
                        List.of(StreamRequestV29::getTitle))
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> removeEventStreamV29() {
        return withStreamIdExpectations(validators.remove(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().removeEventStreamV29())
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> disableEventStreamV29() {
        return withStreamIdExpectations(validators.standard(VERSION)
                .as(OWNER)
                .apiCall(() -> apiClient.streams().disableEventStreamV29())
                .pathParams(() -> freshStreamPathParams(VERSION)), VERSION)
                .tests();
    }
}
