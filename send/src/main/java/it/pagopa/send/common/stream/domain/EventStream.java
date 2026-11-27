package it.pagopa.send.common.stream.domain;

import it.pagopa.domain.Identifiable;

import java.util.UUID;

/**
 * Stream di eventi di avanzamento notifiche configurato dall'ente mittente.
 */
public record EventStream(UUID streamId, String title) implements Identifiable {

    @Override
    public UUID getId() {
        return streamId;
    }
}
