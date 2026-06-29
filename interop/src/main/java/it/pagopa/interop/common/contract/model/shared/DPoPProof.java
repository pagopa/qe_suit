package it.pagopa.interop.common.contract.model.shared;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DPoPProof {
    String jwt;
    Key key;
}