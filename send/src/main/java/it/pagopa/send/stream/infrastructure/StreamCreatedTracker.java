package it.pagopa.send.stream.infrastructure;

import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Registro degli stream creati dai contract test di {@code createEventStream}. Un caso di fuzzing
 * che muta il {@code title} e viene accettato crea uno stream senza il prefisso di test: qui lo si
 * traccia dalla risposta, cosi' che il purge di {@code StreamJourney} possa rimuoverlo.
 */
@Component
public class StreamCreatedTracker {

    private final Set<UUID> trackedStreamIds = ConcurrentHashMap.newKeySet();

    /**
     * Avvolge un'aspettativa: se la risposta contiene uno {@code streamId} (stream effettivamente
     * creato, atteso o no) lo traccia, poi applica l'aspettativa.
     */
    public Consumer<Response> tracking(Consumer<Response> expectation) {
        return response -> {
            track(response);
            expectation.accept(response);
        };
    }

    public boolean isTracked(UUID streamId) {
        return trackedStreamIds.contains(streamId);
    }

    public void forget(UUID streamId) {
        trackedStreamIds.remove(streamId);
    }

    private void track(Response response) {
        if (response.statusCode() / 100 != 2) {
            return;
        }
        try {
            String streamId = response.jsonPath().getString("streamId");
            if (streamId != null) {
                trackedStreamIds.add(UUID.fromString(streamId));
            }
        } catch (RuntimeException ignored) {
            // body non conforme: nessuno stream da tracciare, l'aspettativa decidera' l'esito
        }
    }
}
