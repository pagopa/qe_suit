package it.pagopa.interop.common.kernel.security;

import it.pagopa.interop.common.kernel.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ClientAssertion implements Identifiable {
    String clientAssertion;
    UUID id = UUID.randomUUID();
}
