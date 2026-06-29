package it.pagopa.interop.common.contract.model.eservice;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DescriptorRejectionReason {
    String rejectionReason;
    Instant rejectedAt;
}


