package it.pagopa.interop.new_arch.common.shared.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DelegationRef {
    UUID id;
    DelegationTenantRef delegate;
    DelegationTenantRef delegator;
}


