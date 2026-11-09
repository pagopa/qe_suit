package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;

public interface EServiceTemplateInstanceJourney<SELF extends EServiceTemplateInstanceJourney<SELF>> extends JourneyModule {
    SELF createPublishedEServiceTemplateInstance(
            EServiceTemplate template, EServiceTemplateInstanceCreationCommand command, String alias);
}
