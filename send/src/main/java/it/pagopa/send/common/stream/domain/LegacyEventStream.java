package it.pagopa.send.common.stream.domain;

import it.pagopa.domain.Identifiable;

import java.util.UUID;

/**
 * Stream di eventi di avanzamento notifiche creato dall'ente mittente con le API pn-stream legacy
 * (v1.0, v2.3-v2.9). La versione con cui e' stato creato e' nota al chiamante: il backend rifiuta
 * l'accesso da una versione diversa.
 */
public record LegacyEventStream(UUID streamId, String title) implements Identifiable {

    @Override
    public UUID getId() {
        return streamId;
    }
}
