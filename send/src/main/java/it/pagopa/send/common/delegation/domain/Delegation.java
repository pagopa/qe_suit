package it.pagopa.send.common.delegation.domain;

import it.pagopa.domain.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Delega tra destinatari, vista dal delegato: il {@code mandateId} è l'id della delega usato dalle API e dalle pagine
 * del portale (es. {@code /notifiche-delegato/<mandateId>/<IUN>/dettaglio}).
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class Delegation implements Identifiable {
    String mandateId;
    String delegatorTaxId;
    DelegationStatus status;

    /**
     * {@link Identifiable} richiede un {@link UUID}: derivato dal {@code mandateId}, così la stessa delega ha sempre
     * lo stesso id in {@code EntityStore}.
     */
    @Override
    public UUID getId() {
        return UUID.nameUUIDFromBytes(mandateId.getBytes(StandardCharsets.UTF_8));
    }
}
