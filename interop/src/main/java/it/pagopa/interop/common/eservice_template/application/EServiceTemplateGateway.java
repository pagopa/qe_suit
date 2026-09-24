package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateDescriptionCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateIntendedTargetCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateNameCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateGateway extends Plugin<Channel> {
    EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command);

    EServiceTemplate getEServiceTemplate(EServiceTemplateRef templateRef);

    EServiceTemplate updateEServiceTemplate(EServiceTemplateRef templateRef, UpdateEServiceTemplateCommand command);

    EServiceTemplate updateTemplateName(EServiceTemplateRef templateRef, UpdateEServiceTemplateNameCommand command);

    EServiceTemplate updateTemplateIntendedTarget(EServiceTemplateRef templateRef, UpdateEServiceTemplateIntendedTargetCommand command);

    EServiceTemplate updateTemplateDescription(EServiceTemplateRef templateRef, UpdateEServiceTemplateDescriptionCommand command);
}

