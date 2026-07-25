package it.pagopa.interop.common.kernel.domain;

import java.util.UUID;

public record ClientRef(UUID id) {
    public static ClientRef of(UUID id) {
        return new ClientRef(id);
    }
}
