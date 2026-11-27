package it.pagopa.send.common.stream.application;

import it.pagopa.send.common.stream.domain.EventStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Casi d'uso sugli stream di eventi dell'ente mittente (utenza in sessione). Puro pass-through verso
 * {@link StreamGateway}, piu' la rimozione massiva degli stream che soddisfano un criterio.
 */
@Service
@RequiredArgsConstructor
public class StreamUseCase {

    private final StreamGateway streamGateway;

    public EventStream createStream(String title) {
        return streamGateway.createStream(title);
    }

    public List<EventStream> listStreams() {
        return streamGateway.listStreams();
    }

    public void removeStream(UUID streamId) {
        streamGateway.removeStream(streamId);
    }

    /**
     * Rimuove tutti gli stream che soddisfano il criterio e restituisce quelli rimossi.
     */
    public List<EventStream> removeStreams(Predicate<EventStream> criteria) {
        List<EventStream> toRemove = streamGateway.listStreams().stream().filter(criteria).toList();
        toRemove.forEach(stream -> streamGateway.removeStream(stream.streamId()));
        return toRemove;
    }
}
