package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.tenant.application.command.AssignCertifiedAttributeCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import lombok.Getter;

import java.util.Objects;

public class BffAssignCertifiedAttributeCommand implements AssignCertifiedAttributeCommand {
    @Getter
    private final CertifiedTenantAttributeSeed bffPayload = new CertifiedTenantAttributeSeed();

    @Override
    public AssignCertifiedAttributeCommand attribute(AttributeRef attributeRef) {
        bffPayload.setId(Objects.requireNonNull(attributeRef, "attributeRef must not be null").id());
        return this;
    }
}
