package it.pagopa.interop.common.journey.application;


import it.pagopa.interop.common.kernel.domain.Tenant;

public interface TenantJourney<SELF extends TenantJourney<SELF>> extends JourneyModule {
    SELF assignCertifiedAttribute(Tenant tenant);

}
