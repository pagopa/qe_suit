package it.pagopa.interop.common.journey.application;


public interface AttributeJourney<SELF extends AttributeJourney<SELF>> extends JourneyModule {
    SELF createCertifiedAttribute();

}
