package it.pagopa.interop.common.contract.model.eservice;

import it.pagopa.interop.common.contract.enums.TenantType;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DelegationTenantRef {
    UUID id;
    String name;
    TenantType kind;
}


