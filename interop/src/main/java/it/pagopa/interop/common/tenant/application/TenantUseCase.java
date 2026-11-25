package it.pagopa.interop.common.tenant.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class TenantUseCase {

    private final TenantGateway tenantGateway;

    public void ensureProducerDelegationEnabled(TenantRef tenantRef) {
        tenantGateway.ensureProducerDelegationEnabled(java.util.Objects.requireNonNull(tenantRef));
    }

    public void enableProducerDelegationAvailability(TenantRef tenantRef) {
        tenantGateway.enableProducerDelegationAvailability(tenantRef);
    }

    public void enableConsumerDelegationAvailability(TenantRef tenantRef) {
        tenantGateway.enableConsumerDelegationAvailability(tenantRef);
    }

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

    public void attemptVerifiedAttributeExpirationUpdate(TenantRef tenantRef, AttributeRef attributeRef, OffsetDateTime expirationDate) {
        tenantGateway.attemptVerifiedAttributeExpirationUpdate(tenantRef, attributeRef, expirationDate);
    }

    public void attemptProducerDelegationAvailability(TenantRef tenantRef) {
        tenantGateway.attemptProducerDelegationAvailability(tenantRef);
    }

    public void attemptCertifiedAttributeAssignment(TenantRef tenantRef, Attribute attribute) {
        if (attribute.getKind() == AttributeKind.CERTIFIED) {
            tenantGateway.attemptCertifiedAttributeAssignment(tenantRef, attribute.getRef());
            return;
        }
        if (attribute.getKind() == AttributeKind.CERTIFIED_DISCRETE) {
            tenantGateway.attemptCertifiedDiscreteAttributeAssignment(tenantRef, attribute.getRef(), 1);
            return;
        }
        throw new IllegalArgumentException(
                "Certified attribute assignment attempt requires kind CERTIFIED or CERTIFIED_DISCRETE, but was "
                        + attribute.getKind()
        );
    }

    public void attemptContactEmailSaveWithHyperlinkInNotes(TenantRef tenantRef) {
        tenantGateway.attemptContactEmailSaveWithHyperlinkInNotes(tenantRef);
    }

    public void attemptVerifiedAttributeAssignment(
            TenantRef tenantRef, Attribute attribute, AgreementRef agreementRef, OffsetDateTime expirationDate
    ) {
        if (attribute.getKind() != AttributeKind.VERIFIED) {
            throw new IllegalArgumentException("Attribute assignment attempt requires kind VERIFIED, but was "
                    + attribute.getKind());
        }
        tenantGateway.attemptVerifiedAttributeAssignment(tenantRef, attribute.getRef(), agreementRef, expirationDate);
    }
}
