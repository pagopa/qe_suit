package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.attribute.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;

public interface TenantJourney<SELF extends TenantJourney<SELF>> extends JourneyModule {

    SELF assignCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    SELF assignCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef, int value);

    SELF assignDeclaredAttribute(AttributeRef attributeRef);

    SELF assignVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef);

    SELF revokeDeclaredAttribute(AttributeRef attributeRef);

    SELF revokeCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    SELF revokeCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    SELF revokeVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef);
}
