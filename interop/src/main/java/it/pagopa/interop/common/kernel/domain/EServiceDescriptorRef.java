package it.pagopa.interop.new_arch.common.kernel.domain;

import javax.annotation.Nonnull;
import java.util.UUID;

public record EServiceDescriptorRef(@Nonnull UUID id) {
    public static EServiceDescriptorRef of(UUID id) {
        return new EServiceDescriptorRef(id);
    }
}
