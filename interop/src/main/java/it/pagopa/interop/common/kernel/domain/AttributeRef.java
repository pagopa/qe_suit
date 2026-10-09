package it.pagopa.interop.common.kernel.domain;

import javax.annotation.Nonnull;
import java.util.UUID;

public record AttributeRef(@Nonnull UUID id) {
    public static AttributeRef of(UUID id) {
        return new AttributeRef(id);
    }
}
