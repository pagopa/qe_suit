package it.pagopa.send.common.stream.application;

import it.pagopa.send.common.stream.domain.LegacyEventStream;
import it.pagopa.send.common.stream.domain.StreamApiVersion;

import java.util.UUID;

/**
 * Stream di test pn-stream gestiti con le API legacy (v1.0, v2.3-v2.9), con l'api-key dell'utenza in
 * sessione. Ogni stream va gestito con la versione con cui e' stato creato.
 */
public interface StreamLegacyGateway {

    /**
     * Crea uno stream di test ATTIVO con la versione indicata, lo registra per la pulizia e lo rende
     * disponibile in {@code EntityStore}.
     */
    LegacyEventStream createStream(StreamApiVersion version);

    void disableStream(StreamApiVersion version, UUID streamId);

    /**
     * Rimuove lo stream; uno stream gia' assente non e' un errore.
     */
    void removeStream(StreamApiVersion version, UUID streamId);

    /**
     * Rimuove gli stream creati da questa esecuzione, ciascuno con la propria versione: non tocca gli
     * stream di altre esecuzioni in corso sulla stessa PA.
     */
    void removeCreatedStreams();

    /**
     * Rimuove gli stream di test residui di esecuzioni precedenti, riconosciuti dal titolo.
     */
    void removeLeftoverTestStreams();
}
