package it.pagopa.interop.common.journey.application;


import it.pagopa.interop.common.kernel.domain.Tenant;

public interface AttributeJourney<SELF extends AttributeJourney<SELF>> extends JourneyModule {
    SELF createCertifiedAttribute();

    SELF assignAttribute(Tenant tenant);
}
