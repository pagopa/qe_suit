package it.pagopa.interop.new_arch.common.kernel.domain;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder(toBuilder = true)
@Jacksonized
public record PurposeVersionRef(UUID id) {
    public static PurposeVersionRef of(UUID id) {
        return new PurposeVersionRef(id);
    }
}