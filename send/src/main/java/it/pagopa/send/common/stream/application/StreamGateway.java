package it.pagopa.send.common.stream.application;

import it.pagopa.send.common.stream.domain.EventStream;

import java.util.List;
import java.util.UUID;

public interface StreamGateway {

    EventStream createStream(String title);

    List<EventStream> listStreams();

    /**
     * Rimuove lo stream; uno stream gia' assente non e' un errore.
     */
    void removeStream(UUID streamId);
}
