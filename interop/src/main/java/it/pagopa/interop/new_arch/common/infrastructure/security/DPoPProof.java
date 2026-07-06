package it.pagopa.interop.new_arch.common.infrastructure.security;

import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DPoPProof implements Identifiable {
    String jwt;
    Key key;
    UUID id = UUID.randomUUID();
}