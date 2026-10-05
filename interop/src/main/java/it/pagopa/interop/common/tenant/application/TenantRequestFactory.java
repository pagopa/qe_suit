package it.pagopa.interop.common.tenant.application;

import it.pagopa.interop.common.attribute.domain.AttributeRef;
import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.tenant.application.command.*;
import org.springframework.plugin.core.Plugin;

public interface TenantRequestFactory extends Plugin<Channel> {

    AssignCertifiedAttributeCommand assignCertifiedAttributeCommand(AttributeRef attributeRef);

    AssignCertifiedDiscreteAttributeCommand assignCertifiedDiscreteAttributeCommand(AttributeRef attributeRef, int value);

    AssignDeclaredAttributeCommand assignDeclaredAttributeCommand(AttributeRef attributeRef);

    AssignVerifiedAttributeCommand assignVerifiedAttributeCommand(AttributeRef attributeRef, AgreementRef agreementRef);

    RevokeVerifiedAttributeCommand revokeVerifiedAttributeCommand(AgreementRef agreementRef);
}


