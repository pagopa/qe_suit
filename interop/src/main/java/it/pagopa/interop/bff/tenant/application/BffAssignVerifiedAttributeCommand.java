package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.attribute.domain.AttributeRef;
import it.pagopa.interop.common.tenant.application.command.AssignVerifiedAttributeCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed;
import lombok.Getter;

import java.util.Objects;

public class BffAssignVerifiedAttributeCommand implements AssignVerifiedAttributeCommand {
    @Getter
    private final VerifiedTenantAttributeSeed bffPayload = new VerifiedTenantAttributeSeed();

    @Override
    public AssignVerifiedAttributeCommand attribute(AttributeRef attributeRef) {
        bffPayload.setId(Objects.requireNonNull(attributeRef, "attributeRef must not be null").id());
        return this;
    }

    @Override
    public AssignVerifiedAttributeCommand agreement(AgreementRef agreementRef) {
        bffPayload.setAgreementId(Objects.requireNonNull(Objects.requireNonNull(agreementRef, "agreementRef must not be null").id(), "agreementId must not be null"));
        return this;
    }

    @Override
    public AssignVerifiedAttributeCommand expirationDate(String expirationDate) {
        bffPayload.setExpirationDate(expirationDate);
        return this;
    }
}
