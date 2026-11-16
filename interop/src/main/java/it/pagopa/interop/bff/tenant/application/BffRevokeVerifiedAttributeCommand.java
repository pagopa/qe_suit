package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.tenant.application.command.RevokeVerifiedAttributeCommand;
import lombok.Getter;

import java.util.Objects;

public class BffRevokeVerifiedAttributeCommand implements RevokeVerifiedAttributeCommand {
    @Getter
    private AgreementRef agreement;

    @Override
    public RevokeVerifiedAttributeCommand agreement(AgreementRef agreementRef) {
        this.agreement = Objects.requireNonNull(agreementRef, "AgreementRef cannot be null");
        return this;
    }
}
