package it.pagopa.interop.new_arch.common.kernel.domain;

import it.pagopa.interop.new_arch.common.attribute.domain.Attributes;
import it.pagopa.interop.new_arch.common.kernel.Identifiable;
import lombok.Builder;
import lombok.Value;
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
