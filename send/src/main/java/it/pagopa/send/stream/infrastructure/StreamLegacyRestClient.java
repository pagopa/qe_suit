package it.pagopa.send.stream.infrastructure;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.StreamListElement;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Chiamate alle API pn-stream legacy usate dalle precondizioni, esposte come {@link TestChain}:
 * il gateway sceglie polling e verifiche. Ogni chiamata e' delegata al catalogo per versione
 * {@link StreamLegacyApiOperations}.
 */
@Component
public class StreamLegacyRestClient extends RestClient {

    private final StreamLegacyApiOperations operations;

    public StreamLegacyRestClient(TestChainFactory chainFactory, StreamLegacyApiOperations operations) {
        super(chainFactory);
        this.operations = operations;
    }

    /**
     * Risposta ridotta ai campi comuni a tutte le versioni ({@code streamId}, {@code title}): il modello
     * di risposta generato cambia a ogni versione.
     */
    public TestChain<StreamListElement> create(StreamApiVersion version, String title) {
        return execute(
                () -> operations.of(version).create().apply(title),
                StreamListElement.class,
                StreamLegacyRestClient::streamReference
        );
    }

    public TestChain<StreamListElement[]> list(StreamApiVersion version) {
        return execute(() -> operations.of(version).list().get(), StreamListElement[].class);
    }

    public TestChain<Void> disable(StreamApiVersion version, UUID streamId) {
        return execute(() -> operations.of(version).disable().apply(streamId), Void.class);
    }

    public TestChain<Void> remove(StreamApiVersion version, UUID streamId) {
        return execute(() -> operations.of(version).remove().apply(streamId), Void.class);
    }

    private static StreamListElement streamReference(Response response) {
        JsonPath body = response.jsonPath();
        return new StreamListElement()
                .streamId(UUID.fromString(body.getString("streamId")))
                .title(body.getString("title"));
    }
}
