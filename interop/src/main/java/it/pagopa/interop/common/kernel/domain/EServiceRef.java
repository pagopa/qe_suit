package it.pagopa.interop.new_arch.common.kernel.domain;

import javax.annotation.Nonnull;
import java.util.UUID;

public record EServiceRef(@Nonnull UUID id) {
    public static EServiceRef of(UUID id) {
        return new EServiceRef(id);
    }
}
