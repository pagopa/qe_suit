package it.pagopa.send.common.stream.application;

import it.pagopa.send.common.stream.domain.LegacyEventStream;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Casi d'uso sugli stream legacy dell'ente mittente (utenza in sessione): pass-through verso
 * {@link StreamLegacyGateway}.
 */
@Service
@RequiredArgsConstructor
public class StreamLegacyUseCase {

    private final StreamLegacyGateway streamLegacyGateway;

    public LegacyEventStream createStream(StreamApiVersion version) {
        return streamLegacyGateway.createStream(version);
    }

    public void disableStream(StreamApiVersion version, UUID streamId) {
        streamLegacyGateway.disableStream(version, streamId);
    }

    public void removeStream(StreamApiVersion version, UUID streamId) {
        streamLegacyGateway.removeStream(version, streamId);
    }

    public void removeCreatedStreams() {
        streamLegacyGateway.removeCreatedStreams();
    }

    public void removeLeftoverTestStreams() {
        streamLegacyGateway.removeLeftoverTestStreams();
    }
}
