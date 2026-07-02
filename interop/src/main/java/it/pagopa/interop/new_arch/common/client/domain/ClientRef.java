package it.pagopa.interop.new_arch.common.client.domain;

import java.util.UUID;

public record ClientRef(UUID id) {
    public static ClientRef of(UUID id) {
        return new ClientRef(id);
    }
}
