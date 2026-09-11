package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
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
        return eServiceTemplateGateway.getEServiceTemplate(eServiceTemplate.getId());
    }
}

