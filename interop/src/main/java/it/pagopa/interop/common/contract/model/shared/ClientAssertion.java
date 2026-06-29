package it.pagopa.interop.common.contract.model.shared;

import it.pagopa.interop.common.contract.model.TestModel;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ClientAssertion implements TestModel {
    String clientAssertion;
    UUID id = UUID.randomUUID();
}
