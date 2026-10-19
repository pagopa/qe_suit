package it.pagopa.interop.common.producer_keychain.domain;

import it.pagopa.domain.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/** Metadati di una chiave pubblica registrata nel producer keychain. */
// "orphan=true" solo se si verifica una delle seguenti condizioni:
    // 1. l'utente non è (più) associato al keychain
    // 2. l'utente è stato rimosso dal tenant o non esiste più su Selfcare.
@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProducerKeychainPublicKey implements Identifiable {
    UUID id;
    String name;
    UUID userId;
    String createdAt;
    Boolean orphan;

    public KeyRef getRef() {
        return KeyRef.of(id);
    }
}
