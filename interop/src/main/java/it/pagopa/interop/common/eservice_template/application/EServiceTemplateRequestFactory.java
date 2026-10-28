package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateRequestFactory extends Plugin<Channel> {
    EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand();

    EServiceTemplateInstanceCreationCommand defaultCreationEServiceTemplateInstanceCommand();

    UpdateEServiceTemplateCommand defaultUpdateEServiceTemplateCommand();

    UpdateEServiceTemplateNameCommand defaultUpdateTemplateNameCommand();

    UpdateEServiceTemplateIntendedTargetCommand defaultUpdateTemplateIntendedTargetCommand();

    UpdateEServiceTemplateDescriptionCommand defaultUpdateTemplateDescriptionCommand();

    UpdateEServiceTemplateVersionCommand defaultUpdateTemplateVersionCommand();
}
