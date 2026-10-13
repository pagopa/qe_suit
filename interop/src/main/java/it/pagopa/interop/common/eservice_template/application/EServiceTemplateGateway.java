package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface EServiceTemplateGateway extends Plugin<Channel> {
    EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command);

    EServiceTemplate uploadInterface(EServiceTemplate template, String fixturePath);

    EServiceTemplate publishEServiceTemplateVersion(EServiceTemplate template);

    EServiceTemplate getEServiceTemplateVersion(EServiceTemplate template);

    EServiceTemplateInterface downloadInterface(EServiceTemplate template);

    byte[] downloadInterfaceBytes(EServiceTemplate template, UUID documentId);
}
