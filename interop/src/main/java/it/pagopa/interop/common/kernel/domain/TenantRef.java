package it.pagopa.interop.common.kernel.domain;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public record TenantRef(@Nonnull UUID id) {
    public TenantRef {
        Objects.requireNonNull(id, "id must not be null");
    }

    public static TenantRef of(@Nonnull UUID id) {
        return new TenantRef(id);
    }
}


