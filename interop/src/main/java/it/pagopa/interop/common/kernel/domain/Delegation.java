package it.pagopa.interop.common.kernel.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Delegation {
    UUID id;
    DelegationTenant delegate;
    DelegationTenant delegator;
}


