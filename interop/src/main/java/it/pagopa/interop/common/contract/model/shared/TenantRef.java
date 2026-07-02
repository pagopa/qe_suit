package it.pagopa.interop.common.contract.model.shared;

import it.pagopa.interop.common.contract.model.Identifiable;
import it.pagopa.interop.common.contract.model.attribute.Attributes;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class TenantRef implements Identifiable {
    TenantRef tenant;
    Attributes attributes;

    @Override
    public UUID getId() {
        return tenant.getId();
    }
}
