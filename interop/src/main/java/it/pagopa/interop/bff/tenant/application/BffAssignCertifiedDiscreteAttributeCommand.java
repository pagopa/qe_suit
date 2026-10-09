package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.tenant.application.command.AssignCertifiedDiscreteAttributeCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedDiscreteTenantAttributeSeed;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

public class BffAssignCertifiedDiscreteAttributeCommand implements AssignCertifiedDiscreteAttributeCommand {
    @Getter
    private final CertifiedDiscreteTenantAttributeSeed bffPayload = new CertifiedDiscreteTenantAttributeSeed();

    @Override
    public AssignCertifiedDiscreteAttributeCommand attribute(@NonNull AttributeRef attributeRef) {
        bffPayload.setId(attributeRef.id());
        return this;
    }

    @Override
    public AssignCertifiedDiscreteAttributeCommand value(int value) {
        bffPayload.setCertifiedDiscreteValue(value);
        return this;
    }
}
