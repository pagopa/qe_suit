package it.pagopa.interop.new_arch.common.infrastructure.security;

import it.pagopa.interop.common.contract.model.TestModel;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DPoPProof implements TestModel {
    String jwt;
    Key key;
    UUID id = UUID.randomUUID();
}