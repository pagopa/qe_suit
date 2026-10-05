package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.attribute.domain.AttributeRef;
import it.pagopa.interop.common.tenant.application.command.AssignCertifiedDiscreteAttributeCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedDiscreteTenantAttributeSeed;
import lombok.Getter;

import java.util.Objects;

public class BffAssignCertifiedDiscreteAttributeCommand implements AssignCertifiedDiscreteAttributeCommand {
    @Getter
    private final CertifiedDiscreteTenantAttributeSeed bffPayload = new CertifiedDiscreteTenantAttributeSeed();

    @Override
    public AssignCertifiedDiscreteAttributeCommand attribute(AttributeRef attributeRef) {
        bffPayload.setId(Objects.requireNonNull(attributeRef, "attributeRef must not be null").id());
        return this;
    }

    @Override
    public AssignCertifiedDiscreteAttributeCommand value(int value) {
        bffPayload.setCertifiedDiscreteValue(value);
        return this;
    }
}
