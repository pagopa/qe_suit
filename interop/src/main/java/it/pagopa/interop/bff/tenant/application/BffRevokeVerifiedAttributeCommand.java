package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.tenant.application.command.RevokeVerifiedAttributeCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.RevokeVerifiedAttributeRequest;
import lombok.Getter;

import java.util.Objects;

public class BffRevokeVerifiedAttributeCommand implements RevokeVerifiedAttributeCommand {
    @Getter
    private final RevokeVerifiedAttributeRequest bffPayload = new RevokeVerifiedAttributeRequest();

    @Override
    public RevokeVerifiedAttributeCommand agreement(AgreementRef agreementRef) {
        bffPayload.setAgreementId(Objects.requireNonNull(Objects.requireNonNull(agreementRef, "agreementRef must not be null").id(), "agreementId must not be null"));
        return this;
    }
}
