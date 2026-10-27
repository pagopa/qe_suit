package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;

public interface TenantJourney<SELF extends TenantJourney<SELF>> extends JourneyModule {

    SELF ensureProducerDelegationEnabled(Tenant tenant);

    SELF assignCertifiedAttribute(TenantRef tenantRef);

    SELF assignCertifiedAttribute(TenantRef tenantRef, int nthAttribute);

    SELF assignCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef);

    SELF assignCertifiedDiscreteAttribute(TenantRef tenantRef, int value);

    SELF assignDeclaredAttribute();

    SELF assignVerifiedAttribute(TenantRef tenantRef);

    SELF revokeDeclaredAttribute();

    SELF revokeCertifiedAttribute(TenantRef tenantRef);

    SELF revokeCertifiedDiscreteAttribute(TenantRef tenantRef);

    SELF revokeVerifiedAttribute(TenantRef tenantRef);
}
