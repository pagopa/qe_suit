package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.tenant.application.command.AssignCertifiedAttributeCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

public class BffAssignCertifiedAttributeCommand implements AssignCertifiedAttributeCommand {
    @Getter
    private final CertifiedTenantAttributeSeed bffPayload = new CertifiedTenantAttributeSeed();

    @Override
    public AssignCertifiedAttributeCommand attribute(@NonNull AttributeRef attributeRef) {
        bffPayload.setId(attributeRef.id());
        return this;
    }
}
