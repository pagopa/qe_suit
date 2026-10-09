package it.pagopa.interop.common.tenant.application;

import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import org.springframework.plugin.core.Plugin;

public interface TenantGateway extends Plugin<Channel> {

    void assignCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    void assignCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef, int value);

    void assignDeclaredAttribute(AttributeRef attributeRef);

    void assignVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef);

    void revokeDeclaredAttribute(AttributeRef attributeRef);

    void revokeCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    void revokeCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    void revokeVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef);
}
