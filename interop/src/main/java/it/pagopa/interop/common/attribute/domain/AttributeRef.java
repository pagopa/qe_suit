package it.pagopa.interop.common.attribute.domain;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public record AttributeRef(@Nonnull UUID id) {
    public AttributeRef {
        Objects.requireNonNull(id, "id must not be null");
    }

    public static AttributeRef of(@Nonnull UUID id) {
        return new AttributeRef(id);
    }
}

