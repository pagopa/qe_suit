package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.tenant.application.command.AssignDeclaredAttributeCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

public class BffAssignDeclaredAttributeCommand implements AssignDeclaredAttributeCommand {
    @Getter
    private final DeclaredTenantAttributeSeed bffPayload = new DeclaredTenantAttributeSeed();

    @Override
    public AssignDeclaredAttributeCommand attribute(@NonNull AttributeRef attributeRef) {
        bffPayload.setId(attributeRef.id());
        return this;
    }
}
