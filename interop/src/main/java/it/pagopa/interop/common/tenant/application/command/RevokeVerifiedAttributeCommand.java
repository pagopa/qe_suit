package it.pagopa.interop.common.tenant.application.command;

import it.pagopa.interop.common.agreement.domain.AgreementRef;

public interface RevokeVerifiedAttributeCommand {
    RevokeVerifiedAttributeCommand agreement(AgreementRef agreementRef);
}
