package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.domain.EServiceTechnology;

public interface EServiceTemplateJourney<SELF extends EServiceTemplateJourney<SELF>> extends JourneyModule {
    SELF createPublishedEServiceTemplate(EServiceTechnology technology, String interfaceFixture);
}
