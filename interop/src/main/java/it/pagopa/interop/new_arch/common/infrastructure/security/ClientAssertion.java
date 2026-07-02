package it.pagopa.interop.new_arch.common.infrastructure.security;

import it.pagopa.interop.common.contract.model.Identifiable;
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
