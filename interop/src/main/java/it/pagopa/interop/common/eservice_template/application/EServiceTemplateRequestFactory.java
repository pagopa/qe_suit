package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateRequestFactory extends Plugin<Channel> {
    EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand();

    default EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand(EServiceTemplatePreparationConfiguration configuration) {
        var command = defaultCreationEServiceTemplateCommand();
        command.technology(configuration.getTechnology());
        command.interfaceDocumentPath(configuration.getInterfaceFixture());

        return command;
    }

    UpdateEServiceTemplateCommand defaultUpdateEServiceTemplateCommand();

    UpdateEServiceTemplateNameCommand defaultUpdateTemplateNameCommand();

    UpdateEServiceTemplateIntendedTargetCommand defaultUpdateTemplateIntendedTargetCommand();

    UpdateEServiceTemplateDescriptionCommand defaultUpdateTemplateDescriptionCommand();

    UpdateEServiceTemplateVersionCommand defaultUpdateTemplateVersionCommand();
}
