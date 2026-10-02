package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;

public interface EServiceTemplateJourney<SELF extends EServiceTemplateJourney<SELF>> extends JourneyModule {
    SELF createEServiceTemplate(EServiceTemplateVersionState state);

    SELF instantiateEServiceFromTemplate();

    default SELF createEServiceTemplate() {
        return createEServiceTemplate(EServiceTemplateVersionState.DRAFT);
    }
}

