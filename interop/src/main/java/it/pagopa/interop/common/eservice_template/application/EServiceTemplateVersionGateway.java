package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface EServiceTemplateVersionGateway extends Plugin<Channel> {
    EServiceTemplateVersion getVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId);

    EServiceTemplateVersion publishVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId);

    EServiceTemplateVersion updateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UpdateEServiceTemplateVersionCommand command);

    EServiceTemplateVersion linkOpenApiInterface(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, String openApiInterfacePath);
}

