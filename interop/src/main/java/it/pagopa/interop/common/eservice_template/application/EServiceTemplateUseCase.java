package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateDescriptionCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateIntendedTargetCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateNameCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EServiceTemplateUseCase {
    private final EServiceTemplateGateway eServiceTemplateGateway;
    private final EServiceTemplateRequestFactory requestFactory;

    public EServiceTemplate createEServiceTemplate(Consumer<EServiceTemplateCreationCommand> config) {
        EServiceTemplateCreationCommand creationCommand = requestFactory.defaultCreationEServiceTemplateCommand();
        config.accept(creationCommand);

        return createEServiceTemplate(creationCommand);
    }

    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand creationCommand) {
        return eServiceTemplateGateway.createEServiceTemplate(creationCommand);
    }

    public EServiceTemplate getEServiceTemplate(EServiceTemplate eServiceTemplate) {
        return eServiceTemplateGateway.getEServiceTemplate(eServiceTemplate.getRef());
    }

    public EServiceTemplate updateEServiceTemplate(
            EServiceTemplate eServiceTemplate,
            Consumer<UpdateEServiceTemplateCommand> config
    ) {
        UpdateEServiceTemplateCommand updateCommand = requestFactory.defaultUpdateEServiceTemplateCommand();
        config.accept(updateCommand);

        return updateEServiceTemplate(eServiceTemplate, updateCommand);
    }

    public EServiceTemplate updateEServiceTemplate(
            EServiceTemplate eServiceTemplate,
            UpdateEServiceTemplateCommand command
    ) {
        return eServiceTemplateGateway.updateEServiceTemplate(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate updateTemplateName(
            EServiceTemplate eServiceTemplate,
            Consumer<UpdateEServiceTemplateNameCommand> config
    ) {
        UpdateEServiceTemplateNameCommand command = requestFactory.defaultUpdateTemplateNameCommand();
        config.accept(command);

        return updateTemplateName(eServiceTemplate, command);
    }

    public EServiceTemplate updateTemplateName(
            EServiceTemplate eServiceTemplate,
            UpdateEServiceTemplateNameCommand command
    ) {
        return eServiceTemplateGateway.updateTemplateName(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate updateTemplateIntendedTarget(
            EServiceTemplate eServiceTemplate,
            Consumer<UpdateEServiceTemplateIntendedTargetCommand> config
    ) {
        UpdateEServiceTemplateIntendedTargetCommand command = requestFactory.defaultUpdateTemplateIntendedTargetCommand();
        config.accept(command);

        return updateTemplateIntendedTarget(eServiceTemplate, command);
    }

    public EServiceTemplate updateTemplateIntendedTarget(
            EServiceTemplate eServiceTemplate,
            UpdateEServiceTemplateIntendedTargetCommand command
    ) {
        return eServiceTemplateGateway.updateTemplateIntendedTarget(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate updateTemplateDescription(
            EServiceTemplate eServiceTemplate,
            Consumer<UpdateEServiceTemplateDescriptionCommand> config
    ) {
        UpdateEServiceTemplateDescriptionCommand command = requestFactory.defaultUpdateTemplateDescriptionCommand();
        config.accept(command);

        return updateTemplateDescription(eServiceTemplate, command);
    }

    public EServiceTemplate updateTemplateDescription(
            EServiceTemplate eServiceTemplate,
            UpdateEServiceTemplateDescriptionCommand command
    ) {
        return eServiceTemplateGateway.updateTemplateDescription(eServiceTemplate.getRef(), command);
    }
}

