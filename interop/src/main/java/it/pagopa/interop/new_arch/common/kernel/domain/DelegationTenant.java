package it.pagopa.interop.new_arch.common.kernel.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DelegationTenant {
    UUID id;
    String name;
    TenantKind kind;
}


