package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;

public interface EServiceTemplateJourney<SELF extends EServiceTemplateJourney<SELF>> extends JourneyModule {
    SELF createEServiceTemplate(EServiceTemplateCreationCommand command, EServiceTemplateVersionState state);

    default SELF createEServiceTemplate(EServiceTemplateCreationCommand command) {
        return createEServiceTemplate(command, EServiceTemplateVersionState.DRAFT);
    }

    SELF createEServiceTemplate(EServiceTemplateVersionState state);

    default SELF createEServiceTemplate() {
        return createEServiceTemplate(EServiceTemplateVersionState.DRAFT);
    }
}

