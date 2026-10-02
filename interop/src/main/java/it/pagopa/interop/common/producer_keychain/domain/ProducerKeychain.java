package it.pagopa.interop.common.producer_keychain.domain;

import it.pagopa.kernel.security.Key;
import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.UserRef;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProducerKeychain implements Identifiable {
    UUID id;
    String name;
    String description;
    // KeyPair locale usata per le operazioni crittografiche.
    List<Key> keys;
    // Metadati delle chiavi registrate sulla BFF, senza materiale crittografico.
    List<ProducerKeychainPublicKey> publicKeys;
    Set<UserRef> users;

    public ProducerKeychainRef getRef() {
        return ProducerKeychainRef.of(id);
    }
}