package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.tenant.application.command.AssignDeclaredAttributeCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import lombok.Getter;

import java.util.Objects;

public class BffAssignDeclaredAttributeCommand implements AssignDeclaredAttributeCommand {
    @Getter
    private final DeclaredTenantAttributeSeed bffPayload = new DeclaredTenantAttributeSeed();

    @Override
    public AssignDeclaredAttributeCommand attribute(AttributeRef attributeRef) {
        bffPayload.setId(Objects.requireNonNull(attributeRef, "attributeRef must not be null").id());
        return this;
    }
}
