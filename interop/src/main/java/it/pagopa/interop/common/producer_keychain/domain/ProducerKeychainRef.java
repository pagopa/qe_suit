package it.pagopa.interop.common.producer_keychain.domain;

import javax.annotation.Nonnull;
import java.util.UUID;

public record ProducerKeychainRef(@Nonnull UUID id) {
    public static ProducerKeychainRef of(UUID id) {
        return new ProducerKeychainRef(id);
    }
}
