package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.attribute.domain.Attributes;

public interface AttributeJourney<SELF extends AttributeJourney<SELF>> extends JourneyModule {
    SELF createCertifiedAttribute();

    // TODO
    // il tipo restituito deve essere SELF
    Attributes certifiedAttributesWithDailyCallsPerConsumer(int dailyCallsPerConsumer);
}
