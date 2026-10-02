package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateDescriptionCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateIntendedTargetCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateNameCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateRequestFactory extends Plugin<Channel> {
    EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand();

    UpdateEServiceTemplateCommand defaultUpdateEServiceTemplateCommand();

    UpdateEServiceTemplateNameCommand defaultUpdateTemplateNameCommand();

    UpdateEServiceTemplateIntendedTargetCommand defaultUpdateTemplateIntendedTargetCommand();

    UpdateEServiceTemplateDescriptionCommand defaultUpdateTemplateDescriptionCommand();

    UpdateEServiceTemplateVersionCommand defaultUpdateTemplateVersionCommand();
}

