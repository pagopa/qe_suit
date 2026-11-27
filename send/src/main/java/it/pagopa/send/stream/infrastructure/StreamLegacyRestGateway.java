package it.pagopa.send.stream.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.PollingAction;
import it.pagopa.send.common.stream.application.StreamLegacyGateway;
import it.pagopa.send.common.stream.domain.LegacyEventStream;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.generated.openapi.clients.streamlegacy.model.StreamListElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementazione di {@link StreamLegacyGateway} sulle API B2B pn-stream legacy, tramite
 * {@link StreamLegacyRestClient}. Owner degli stream creati: titolo di test con la versione
 * ({@link StreamLegacyRequestFactory#testStreamTitle}), registrazione per la pulizia e
 * sincronizzazione in {@link EntityStore}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StreamLegacyRestGateway implements StreamLegacyGateway {

    /**
     * Versione usata per elencare gli stream: la list restituisce comunque tutti quelli dell'ente.
     */
    private static final StreamApiVersion LIST_VERSION = StreamApiVersion.V29;

    private final StreamLegacyRestClient restClient;
    private final StreamLegacyRequestFactory requestFactory;
    private final StreamLegacyCreatedTracker createdTracker;
    private final EntityStore entityStore;

    @Override
    public LegacyEventStream createStream(StreamApiVersion version) {
        PollingAction<StreamListElement> created = restClient.create(version, requestFactory.testStreamTitle(version))
                .withoutPolling();
        created.assertStatusCode(StreamLegacyStatusCodes.SUCCESS);
        StreamListElement element = Objects.requireNonNull(created.get(), "Risposta di creazione stream vuota (" + version + ")");
        createdTracker.track(version, element.getStreamId());
        LegacyEventStream stream = new LegacyEventStream(element.getStreamId(), element.getTitle());
        entityStore.upsert(stream);
        log.info("Stream {} ({}) creato", stream.streamId(), version);
        return stream;
    }

    @Override
    public void disableStream(StreamApiVersion version, UUID streamId) {
        restClient.disable(version, streamId).withoutPolling().assertStatusCode(StreamLegacyStatusCodes.SUCCESS);
    }

    @Override
    public void removeStream(StreamApiVersion version, UUID streamId) {
        ApiResponse response = (ApiResponse) restClient.remove(version, streamId).withoutPolling().getRaw();
        int status = response.getStatusCode();
        if (status != StreamLegacyStatusCodes.REMOVE_SUCCESS
                && !StreamLegacyStatusCodes.isStreamNotFound(version, status, response.getRawContent())) {
            throw new IllegalStateException("Rimozione dello stream " + streamId + " (" + version + ") fallita con status "
                    + status + ": " + response.getRawContent());
        }
        createdTracker.forget(streamId);
    }

    @Override
    public void removeCreatedStreams() {
        removeAll(createdTracker.trackedStreams());
    }

    /**
     * Rimuove gli stream di test residui ({@code QE-LEGACY-<versione>-<epochMillis>-}), ciascuno con la
     * versione letta dal titolo, solo se creati da almeno due ore
     * ({@link StreamLegacyRequestFactory#leftoverVersion}): gli stream di un'esecuzione parallela in corso
     * sulla stessa PA restano intatti.
     */
    @Override
    public void removeLeftoverTestStreams() {
        PollingAction<StreamListElement[]> listed = restClient.list(LIST_VERSION).withoutPolling();
        listed.assertStatusCode(StreamLegacyStatusCodes.SUCCESS);
        StreamListElement[] streams = Objects.requireNonNull(listed.get(), "Risposta della list degli stream vuota");
        Instant now = Instant.now();
        Map<UUID, StreamApiVersion> leftovers = new LinkedHashMap<>();
        for (StreamListElement element : streams) {
            requestFactory.leftoverVersion(element.getTitle(), now)
                    .ifPresent(version -> leftovers.put(element.getStreamId(), version));
        }
        removeAll(leftovers);
    }

    /**
     * Le rimozioni sono indipendenti: si tentano tutte e i fallimenti si segnalano insieme alla fine,
     * cosi' che un errore non lasci attivi gli altri stream (limite di stream attivi per PA).
     */
    private void removeAll(Map<UUID, StreamApiVersion> streams) {
        List<String> failures = new ArrayList<>();
        streams.forEach((streamId, version) -> {
            try {
                removeStream(version, streamId);
            } catch (RuntimeException e) {
                failures.add(streamId + " (" + version + "): " + e.getMessage());
            }
        });
        if (!failures.isEmpty()) {
            throw new IllegalStateException("Pulizia degli stream legacy incompleta, " + failures.size()
                    + " rimozioni fallite: " + failures);
        }
    }
}
