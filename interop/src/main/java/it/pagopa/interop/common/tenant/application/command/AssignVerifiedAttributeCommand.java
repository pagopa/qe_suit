package it.pagopa.interop.common.tenant.application.command;

import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.attribute.domain.AttributeRef;

public interface AssignVerifiedAttributeCommand {
    AssignVerifiedAttributeCommand attribute(AttributeRef attributeRef);
    AssignVerifiedAttributeCommand agreement(AgreementRef agreementRef);
    AssignVerifiedAttributeCommand expirationDate(String expirationDate);
}
