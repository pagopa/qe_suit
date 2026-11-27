package it.pagopa.send.stream.infrastructure;

import io.restassured.response.Response;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Registro degli stream legacy creati dai test, con la loro versione: quelli delle precondizioni e
 * quelli creati dai contract test di {@code createEventStream}. Un caso di fuzzing che muta il
 * {@code title} e viene accettato (create o update) lascia uno stream senza il prefisso di test: il
 * registro permette alla purge di rimuoverlo comunque, con la versione con cui e' stato creato.
 */
@Component
public class StreamLegacyCreatedTracker {

    private final Map<UUID, StreamApiVersion> trackedStreams = new ConcurrentHashMap<>();

    /**
     * Avvolge l'aspettativa di una create: una risposta 2xx (stream effettivamente creato, atteso o no)
     * viene tracciata con la versione indicata, poi si applica l'aspettativa. Una risposta 2xx senza
     * {@code streamId} valido fa fallire il caso: lo stream creato non potrebbe essere rimosso.
     */
    public Consumer<Response> tracking(StreamApiVersion version, Consumer<Response> expectation) {
        return response -> {
            track(version, response);
            expectation.accept(response);
        };
    }

    /**
     * Traccia uno stream creato da una precondizione: un update che ne muta il titolo gli toglie il
     * prefisso di test, ma la purge lo trova comunque.
     */
    public void track(StreamApiVersion version, UUID streamId) {
        trackedStreams.put(streamId, version);
    }

    /**
     * Copia degli stream tracciati, con la loro versione.
     */
    public Map<UUID, StreamApiVersion> trackedStreams() {
        return Map.copyOf(trackedStreams);
    }

    public void forget(UUID streamId) {
        trackedStreams.remove(streamId);
    }

    private void track(StreamApiVersion version, Response response) {
        if (response.statusCode() / 100 != 2) {
            return;
        }
        UUID streamId;
        try {
            streamId = UUID.fromString(response.jsonPath().getString("streamId"));
        } catch (RuntimeException e) {
            throw new IllegalStateException("Risposta " + response.statusCode() + " di creazione stream (" + version
                    + ") senza streamId valido, stream non tracciabile per la pulizia: " + response.asString(), e);
        }
        trackedStreams.put(streamId, version);
    }
}
