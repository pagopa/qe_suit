package it.pagopa.interop.common.tenant.application.command;

import it.pagopa.interop.common.kernel.domain.AgreementRef;

public interface RevokeVerifiedAttributeCommand {
    RevokeVerifiedAttributeCommand agreement(AgreementRef agreementRef);
}
