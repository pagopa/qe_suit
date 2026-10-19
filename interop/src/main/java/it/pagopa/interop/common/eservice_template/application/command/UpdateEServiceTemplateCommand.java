package it.pagopa.interop.common.eservice_template.application.command;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;

public interface UpdateEServiceTemplateCommand {
    UpdateEServiceTemplateCommand name(String name);

    UpdateEServiceTemplateCommand intendedTarget(String intendedTarget);

    UpdateEServiceTemplateCommand description(String description);

    UpdateEServiceTemplateCommand technology(EServiceTechnology technology);

    UpdateEServiceTemplateCommand mode(EServiceMode mode);

    UpdateEServiceTemplateCommand isSignalHubEnabled(Boolean isSignalHubEnabled);

    UpdateEServiceTemplateCommand handlePersonalData(Boolean handlePersonalData);

    UpdateEServiceTemplateCommand isAsync(Boolean isAsync);
}

