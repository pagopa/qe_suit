package it.pagopa.interop.common.producer_keychain.domain;

import java.util.List;

public record ProducerKeysPage(List<ProducerKeychainPublicKey> keys, int offset, int limit, Integer totalCount) {
    // Costruttore compatto del record: crea una copia difensiva immutabile e garantisce che "keys" non sia null.
    public ProducerKeysPage {
        keys = List.copyOf(keys);
    }
}
