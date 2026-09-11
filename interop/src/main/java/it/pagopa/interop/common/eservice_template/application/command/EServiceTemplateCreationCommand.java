package it.pagopa.interop.common.eservice_template.application.command;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;

public interface EServiceTemplateCreationCommand {
    EServiceTemplateCreationCommand name(String name);
    EServiceTemplateCreationCommand intendedTarget(String intendedTarget);
    EServiceTemplateCreationCommand description(String description);
    EServiceTemplateCreationCommand technology(EServiceTechnology technology);
    EServiceTemplateCreationCommand mode(EServiceMode mode);
}

