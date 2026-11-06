package it.pagopa.send.common.api_key.domain;

import it.pagopa.domain.Identifiable;
import lombok.Builder;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * API Key di un ente mittente. Il valore della chiave è un segreto e non fa parte del dominio.
 */
@Value
@Builder(toBuilder = true)
public class ApiKey implements Identifiable {
    String apiKeyId;
    String name;

    /**
     * {@link Identifiable} richiede un {@link UUID}: derivato dall'id assegnato da SEND, così la stessa API Key produce
     * sempre lo stesso id in {@code EntityStore}.
     */
    @Override
    public UUID getId() {
        return UUID.nameUUIDFromBytes(apiKeyId.getBytes(StandardCharsets.UTF_8));
    }
}
