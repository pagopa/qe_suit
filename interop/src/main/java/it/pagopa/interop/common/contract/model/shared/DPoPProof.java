package it.pagopa.interop.common.contract.model.shared;

import it.pagopa.interop.common.contract.model.Identifiable;
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