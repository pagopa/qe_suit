package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;

public interface AttributeJourney<SELF extends AttributeJourney<SELF>> extends JourneyModule {
    SELF createCertifiedAttribute();

    Attribute createCertifiedAttribute(String name);

    Attribute createAttribute(AttributeKind kind, String name);
}
