package it.pagopa.interop.new_arch.common.eservice.domain;

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


