package it.pagopa.interop.common.client.domain;

import it.pagopa.domain.Identifiable;
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
