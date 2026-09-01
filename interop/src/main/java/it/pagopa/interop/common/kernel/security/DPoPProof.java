package it.pagopa.interop.common.kernel.security;

import it.pagopa.interop.common.kernel.domain.Key;
import it.pagopa.kernel.domain.Identifiable;
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