package it.pagopa.send.common.radd.domain;

import it.pagopa.domain.Identifiable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Esito della procedura di upload del file ZIP dei documenti di identita' ({@code documentUpload} + PUT sulla
 * presigned url): sono i valori da riportare nella successiva start transaction con lo stesso {@code operationId}.
 *
 * @param operationId  identificativo dell'operazione RADD con cui e' stato richiesto l'upload
 * @param fileKey      chiave del file restituita da {@code documentUpload}
 * @param checksum     SHA-256 del file ZIP, codificato in base64
 * @param versionToken header {@code x-amz-version-id} della risposta alla PUT
 */
public record RaddUpload(String operationId, String fileKey, String checksum, String versionToken)
        implements Identifiable {

    @Override
    public UUID getId() {
        return UUID.nameUUIDFromBytes(operationId.getBytes(StandardCharsets.UTF_8));
    }
}
