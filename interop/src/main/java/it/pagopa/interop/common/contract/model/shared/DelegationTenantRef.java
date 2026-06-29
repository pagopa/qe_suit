package it.pagopa.interop.common.contract.model.shared;

import it.pagopa.interop.common.contract.model.shared.enums.TenantKind;
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
    TenantKind kind;
}


