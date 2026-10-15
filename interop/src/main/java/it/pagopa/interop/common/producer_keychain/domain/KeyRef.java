package it.pagopa.interop.common.producer_keychain.domain;

import javax.annotation.Nonnull;
import java.util.UUID;

public record KeyRef(@Nonnull UUID id) {
    public static KeyRef of(UUID id) {
        return new KeyRef(id);
    }
}
