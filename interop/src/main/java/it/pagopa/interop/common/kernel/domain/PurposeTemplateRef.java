package it.pagopa.interop.common.kernel.domain;

import javax.annotation.Nonnull;
import java.util.UUID;

public record PurposeTemplateRef(@Nonnull UUID id) {
    public static PurposeTemplateRef of(UUID id) {
        return new PurposeTemplateRef(id);
    }
}

