package it.pagopa.interop.common.tenant.application;

import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantUseCase {

    private final TenantGateway tenantGateway;

    public void assignCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef) {
        tenantGateway.assignCertifiedAttribute(tenantRef, attributeRef);
    }

    public void assignCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef, int value) {
        tenantGateway.assignCertifiedDiscreteAttribute(tenantRef, attributeRef, value);
    }

    public void assignDeclaredAttribute(AttributeRef attributeRef) {
        tenantGateway.assignDeclaredAttribute(attributeRef);
    }

    public void assignVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef) {
        tenantGateway.assignVerifiedAttribute(tenantRef, attributeRef, agreementRef);
    }

    public void revokeDeclaredAttribute(AttributeRef attributeRef) {
        tenantGateway.revokeDeclaredAttribute(attributeRef);
    }

    public void revokeCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef) {
        tenantGateway.revokeCertifiedAttribute(tenantRef, attributeRef);
    }

    public void revokeCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef) {
        tenantGateway.revokeCertifiedDiscreteAttribute(tenantRef, attributeRef);
    }

    public void revokeVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef) {
        tenantGateway.revokeVerifiedAttribute(tenantRef, attributeRef, agreementRef);
    }
}
