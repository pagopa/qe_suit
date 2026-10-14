package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EServiceTemplateUseCase {
    private final EServiceTemplateGateway gateway;
    private final EServiceTemplateRequestFactory requestFactory;

    public EServiceTemplate createEServiceTemplate(Consumer<EServiceTemplateCreationCommand> config) {
        EServiceTemplateCreationCommand command = requestFactory.defaultCreationEServiceTemplateCommand();
        config.accept(command);
        return createEServiceTemplate(command);
    }

    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command) {
        return gateway.createEServiceTemplate(command);
    }

    public EServiceTemplate uploadInterface(EServiceTemplate template, String fixturePath) {
        return gateway.uploadInterface(template, fixturePath);
    }

    public EServiceTemplate publishEServiceTemplateVersion(EServiceTemplate template) {
        return gateway.publishEServiceTemplateVersion(template);
    }

    public EServiceTemplate getEServiceTemplateVersion(EServiceTemplate template) {
        return gateway.getEServiceTemplateVersion(template);
    }

    public EServiceTemplateInterface downloadInterface(EServiceTemplate template) {
        return gateway.downloadInterface(template);
    }

    public byte[] downloadInterfaceBytes(EServiceTemplate template, UUID documentId) {
        return gateway.downloadInterfaceBytes(template, documentId);
    }
}
