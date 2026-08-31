package it.pagopa.interop.common.kernel.domain;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder(toBuilder = true)
@Jacksonized
public record PurposeRef(UUID id) {
    public static PurposeRef of(UUID id) {
        return new PurposeRef(id);
    }
}