package it.pagopa.interop.common.kernel.domain;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import javax.annotation.Nonnull;
import java.util.UUID;

@Builder(toBuilder = true)
@Jacksonized
public record DelegationRef(@Nonnull UUID id) {
    public static DelegationRef of(UUID id) {
        return new DelegationRef(id);
    }
}
