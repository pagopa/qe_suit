package it.pagopa.interop.bff.tenant.application;

import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.tenant.application.command.RevokeVerifiedAttributeCommand;
import lombok.Getter;

import java.util.Objects;
import lombok.NonNull;

public class BffRevokeVerifiedAttributeCommand implements RevokeVerifiedAttributeCommand {
    @Getter
    private AgreementRef agreement;

    @Override
    public RevokeVerifiedAttributeCommand agreement(@NonNull AgreementRef agreementRef) {
        Objects.requireNonNull(agreementRef.id(), "agreementId cannot be null");
        this.agreement = agreementRef;
        return this;
    }
}
