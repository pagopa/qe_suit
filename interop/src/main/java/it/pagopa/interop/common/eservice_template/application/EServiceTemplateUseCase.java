package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EServiceTemplateUseCase {
    public static final String STANDARD_INTERFACE_PATH = "assets/origin-interface.yaml";

    private final EServiceTemplateGateway gateway;
    private final EServiceTemplateRequestFactory requestFactory;

    public EServiceTemplate createEServiceTemplate(Consumer<EServiceTemplateCreationCommand> config) {
        EServiceTemplateCreationCommand command = requestFactory.defaultCreationEServiceTemplateCommand();
        config.accept(command);
        return gateway.createEServiceTemplate(command);
    }

    public EServiceTemplate createEServiceTemplate() {
        return createEServiceTemplate(command -> {
        });
    }

    public EServiceTemplate linkStandardInterface(EServiceTemplate template) {
        return gateway.linkOpenApiInterface(template.getRef(), template.lastVersion().getId(), STANDARD_INTERFACE_PATH);
    }

    public EServiceTemplate publishLastVersion(EServiceTemplate template) {
        return gateway.publishVersion(template.getRef(), template.lastVersion().getId());
    }

    public EServiceTemplate getEServiceTemplate(EServiceTemplate template) {
        return gateway.getEServiceTemplate(template.getRef(), template.lastVersion().getId());
    }

    public EService instantiateEService(EServiceTemplate template) {
        return gateway.instantiateEService(template.getRef());
    }
}

