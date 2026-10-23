package it.pagopa.interop.common.tenant.application;

import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import org.springframework.plugin.core.Plugin;

import java.util.Set;
import java.util.UUID;

public interface TenantGateway extends Plugin<Channel> {

    void ensureProducerDelegationEnabled(TenantRef tenantRef);

    Set<UUID> getActiveCertifiedAttributeIds(TenantRef tenantRef);

    Set<UUID> getActiveAttributeIds(TenantRef tenantRef, AttributeKind kind);

    void assignCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    void assignCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef, int value);

    void assignDeclaredAttribute(AttributeRef attributeRef);

    void assignVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef);

    void revokeDeclaredAttribute(AttributeRef attributeRef);

    void revokeCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    void revokeCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    void revokeVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef);
}
