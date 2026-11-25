package it.pagopa.send.common.stream.infrastructure;

import io.restassured.response.Response;
import it.pagopa.send.common.stream.application.StreamGateway;
import it.pagopa.send.common.stream.domain.EventStream;
import it.pagopa.send.generated.openapi.clients.stream.ApiClient;
import it.pagopa.send.generated.openapi.clients.stream.model.StreamCreationRequestV30;
import it.pagopa.send.generated.openapi.clients.stream.model.StreamMetadataResponseV30;
import it.pagopa.send.stream.infrastructure.StreamRequestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Implementazione di {@link StreamGateway} sulle API B2B pn-stream v3.0, con l'api-key dell'utenza
 * in sessione.
 */
@Component
public class StreamRestGateway implements StreamGateway {

    private final ApiClient apiClient;
    private final StreamRequestFactory requestFactory;

    public StreamRestGateway(
            @Qualifier("streamApiClient") ApiClient apiClient,
            StreamRequestFactory requestFactory
    ) {
        this.apiClient = apiClient;
        this.requestFactory = requestFactory;
    }

    @Override
    public EventStream createStream(String title) {
        StreamCreationRequestV30 request = requestFactory.creationRequest().title(title);
        StreamMetadataResponseV30 created = apiClient.streams().createEventStreamV30()
                .body(request)
                .executeAs(r -> r.then().statusCode(200).extract().response());
        return new EventStream(created.getStreamId(), created.getTitle());
    }

    @Override
    public List<EventStream> listStreams() {
        return apiClient.streams().listEventStreamsV30()
                .executeAs(r -> r.then().statusCode(200).extract().response())
                .stream()
                .map(element -> new EventStream(element.getStreamId(), element.getTitle()))
                .toList();
    }

    @Override
    public void removeStream(UUID streamId) {
        Response response = apiClient.streams().removeEventStreamV30()
                .streamIdPath(streamId)
                .execute(r -> r);
        int status = response.statusCode();
        if (status != 204 && status != 404) {
            throw new IllegalStateException(
                    "Rimozione dello stream " + streamId + " fallita con status " + status + ": " + response.asString());
        }
    }
}
