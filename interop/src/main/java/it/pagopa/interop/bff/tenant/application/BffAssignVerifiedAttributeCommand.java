package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.tenant.application.command.AssignVerifiedAttributeCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

public class BffAssignVerifiedAttributeCommand implements AssignVerifiedAttributeCommand {
    @Getter
    private final VerifiedTenantAttributeSeed bffPayload = new VerifiedTenantAttributeSeed();

    @Override
    public AssignVerifiedAttributeCommand attribute(@NonNull AttributeRef attributeRef) {
        bffPayload.setId(attributeRef.id());
        return this;
    }

    @Override
    public AssignVerifiedAttributeCommand agreement(@NonNull AgreementRef agreementRef) {
        bffPayload.setAgreementId(agreementRef.id());
        return this;
    }

    @Override
    public AssignVerifiedAttributeCommand expirationDate(String expirationDate) {
        bffPayload.setExpirationDate(expirationDate);
        return this;
    }
}
