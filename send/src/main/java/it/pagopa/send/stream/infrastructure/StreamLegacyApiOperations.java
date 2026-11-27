package it.pagopa.send.stream.infrastructure;

import io.restassured.response.Response;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.generated.openapi.clients.streamlegacy.ApiClient;
import it.pagopa.send.generated.openapi.clients.streamlegacy.api.EventsApi;
import it.pagopa.send.generated.openapi.clients.streamlegacy.api.StreamsApi;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static it.pagopa.send.common.stream.domain.StreamApiVersion.*;

/**
 * Catalogo delle chiamate al client generato delle API pn-stream legacy, indicizzate per versione,
 * con la richiesta baseline di {@link StreamLegacyRequestFactory}. Restituiscono la {@link Response}
 * grezza: lo usano {@link StreamLegacyRestClient} (precondizioni, tramite TestChain) e i contract test
 * sulle transizioni di stato, che verificano lo status direttamente.
 * <p>
 * Ogni operazione e' materializzata solo all'invocazione, cosi' da usare l'utenza in sessione in
 * quel momento. In v1.0 {@code disable} e {@code replacedStreamId} non esistono.
 * <p>
 * Le operazioni da v1.0 a v2.8 sono {@code deprecated} nello spec (v2.9 no): chiamarle e' lo scopo del
 * catalogo, percio' il warning di deprecazione e' soppresso.
 */
@Component
@SuppressWarnings("deprecation")
public class StreamLegacyApiOperations {

    private final ApiClient apiClient;
    private final StreamLegacyRequestFactory requests;

    public StreamLegacyApiOperations(
            @Qualifier("streamLegacyApiClient") ApiClient apiClient,
            StreamLegacyRequestFactory requests
    ) {
        this.apiClient = apiClient;
        this.requests = requests;
    }

    public record Operations(
            Function<String, Response> create,
            Function<UUID, Response> createReplacing,
            Supplier<Response> list,
            Function<UUID, Response> retrieve,
            Function<UUID, Response> update,
            Function<UUID, Response> disable,
            Function<UUID, Response> remove,
            Function<UUID, Response> consume
    ) {
    }

    public Operations of(StreamApiVersion version) {
        return switch (version) {
            case V10 -> new Operations(
                    t -> s().createEventStream().body(requests.creationRequest(V10, StreamCreationRequest.class).title(t)).execute(r -> r),
                    id -> unsupportedInV10("replacedStreamId"),
                    () -> s().listEventStreams().execute(r -> r),
                    id -> s().retrieveEventStream().streamIdPath(id).execute(r -> r),
                    id -> s().updateEventStream().streamIdPath(id).body(requests.updateRequest(V10, StreamCreationRequest.class)).execute(r -> r),
                    id -> unsupportedInV10("disable"),
                    id -> s().removeEventStream().streamIdPath(id).execute(r -> r),
                    id -> e().consumeEventStream().streamIdPath(id).execute(r -> r));
            case V23 -> new Operations(
                    t -> s().createEventStreamV23().body(requests.creationRequest(V23, StreamCreationRequestV23.class).title(t)).execute(r -> r),
                    id -> s().createEventStreamV23().body(requests.creationRequest(V23, StreamCreationRequestV23.class).replacedStreamId(id)).execute(r -> r),
                    () -> s().listEventStreamsV23().execute(r -> r),
                    id -> s().retrieveEventStreamV23().streamIdPath(id).execute(r -> r),
                    id -> s().updateEventStreamV23().streamIdPath(id).body(requests.updateRequest(V23, StreamRequestV23.class)).execute(r -> r),
                    id -> s().disableEventStreamV23().streamIdPath(id).execute(r -> r),
                    id -> s().removeEventStreamV23().streamIdPath(id).execute(r -> r),
                    id -> e().consumeEventStreamV23().streamIdPath(id).execute(r -> r));
            case V24 -> new Operations(
                    t -> s().createEventStreamV24().body(requests.creationRequest(V24, StreamCreationRequestV24.class).title(t)).execute(r -> r),
                    id -> s().createEventStreamV24().body(requests.creationRequest(V24, StreamCreationRequestV24.class).replacedStreamId(id)).execute(r -> r),
                    () -> s().listEventStreamsV24().execute(r -> r),
                    id -> s().retrieveEventStreamV24().streamIdPath(id).execute(r -> r),
                    id -> s().updateEventStreamV24().streamIdPath(id).body(requests.updateRequest(V24, StreamRequestV24.class)).execute(r -> r),
                    id -> s().disableEventStreamV24().streamIdPath(id).execute(r -> r),
                    id -> s().removeEventStreamV24().streamIdPath(id).execute(r -> r),
                    id -> e().consumeEventStreamV24().streamIdPath(id).execute(r -> r));
            case V25 -> new Operations(
                    t -> s().createEventStreamV25().body(requests.creationRequest(V25, StreamCreationRequestV25.class).title(t)).execute(r -> r),
                    id -> s().createEventStreamV25().body(requests.creationRequest(V25, StreamCreationRequestV25.class).replacedStreamId(id)).execute(r -> r),
                    () -> s().listEventStreamsV25().execute(r -> r),
                    id -> s().retrieveEventStreamV25().streamIdPath(id).execute(r -> r),
                    id -> s().updateEventStreamV25().streamIdPath(id).body(requests.updateRequest(V25, StreamRequestV25.class)).execute(r -> r),
                    id -> s().disableEventStreamV25().streamIdPath(id).execute(r -> r),
                    id -> s().removeEventStreamV25().streamIdPath(id).execute(r -> r),
                    id -> e().consumeEventStreamV25().streamIdPath(id).execute(r -> r));
            case V26 -> new Operations(
                    t -> s().createEventStreamV26().body(requests.creationRequest(V26, StreamCreationRequestV26.class).title(t)).execute(r -> r),
                    id -> s().createEventStreamV26().body(requests.creationRequest(V26, StreamCreationRequestV26.class).replacedStreamId(id)).execute(r -> r),
                    () -> s().listEventStreamsV26().execute(r -> r),
                    id -> s().retrieveEventStreamV26().streamIdPath(id).execute(r -> r),
                    id -> s().updateEventStreamV26().streamIdPath(id).body(requests.updateRequest(V26, StreamRequestV26.class)).execute(r -> r),
                    id -> s().disableEventStreamV26().streamIdPath(id).execute(r -> r),
                    id -> s().removeEventStreamV26().streamIdPath(id).execute(r -> r),
                    id -> e().consumeEventStreamV26().streamIdPath(id).execute(r -> r));
            case V27 -> new Operations(
                    t -> s().createEventStreamV27().body(requests.creationRequest(V27, StreamCreationRequestV27.class).title(t)).execute(r -> r),
                    id -> s().createEventStreamV27().body(requests.creationRequest(V27, StreamCreationRequestV27.class).replacedStreamId(id)).execute(r -> r),
                    () -> s().listEventStreamsV27().execute(r -> r),
                    id -> s().retrieveEventStreamV27().streamIdPath(id).execute(r -> r),
                    id -> s().updateEventStreamV27().streamIdPath(id).body(requests.updateRequest(V27, StreamRequestV27.class)).execute(r -> r),
                    id -> s().disableEventStreamV27().streamIdPath(id).execute(r -> r),
                    id -> s().removeEventStreamV27().streamIdPath(id).execute(r -> r),
                    id -> e().consumeEventStreamV27().streamIdPath(id).execute(r -> r));
            case V28 -> new Operations(
                    t -> s().createEventStreamV28().body(requests.creationRequest(V28, StreamCreationRequestV28.class).title(t)).execute(r -> r),
                    id -> s().createEventStreamV28().body(requests.creationRequest(V28, StreamCreationRequestV28.class).replacedStreamId(id)).execute(r -> r),
                    () -> s().listEventStreamsV28().execute(r -> r),
                    id -> s().retrieveEventStreamV28().streamIdPath(id).execute(r -> r),
                    id -> s().updateEventStreamV28().streamIdPath(id).body(requests.updateRequest(V28, StreamRequestV28.class)).execute(r -> r),
                    id -> s().disableEventStreamV28().streamIdPath(id).execute(r -> r),
                    id -> s().removeEventStreamV28().streamIdPath(id).execute(r -> r),
                    id -> e().consumeEventStreamV28().streamIdPath(id).execute(r -> r));
            case V29 -> new Operations(
                    t -> s().createEventStreamV29().body(requests.creationRequest(V29, StreamCreationRequestV29.class).title(t)).execute(r -> r),
                    id -> s().createEventStreamV29().body(requests.creationRequest(V29, StreamCreationRequestV29.class).replacedStreamId(id)).execute(r -> r),
                    () -> s().listEventStreamsV29().execute(r -> r),
                    id -> s().retrieveEventStreamV29().streamIdPath(id).execute(r -> r),
                    id -> s().updateEventStreamV29().streamIdPath(id).body(requests.updateRequest(V29, StreamRequestV29.class)).execute(r -> r),
                    id -> s().disableEventStreamV29().streamIdPath(id).execute(r -> r),
                    id -> s().removeEventStreamV29().streamIdPath(id).execute(r -> r),
                    id -> e().consumeEventStreamV29().streamIdPath(id).execute(r -> r));
        };
    }

    private StreamsApi s() {
        return apiClient.streams();
    }

    private EventsApi e() {
        return apiClient.events();
    }

    private static Response unsupportedInV10(String feature) {
        throw new UnsupportedOperationException(feature + " non esiste nelle API pn-stream v1.0");
    }
}
