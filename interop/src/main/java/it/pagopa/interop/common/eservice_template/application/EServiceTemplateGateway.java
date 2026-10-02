package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface EServiceTemplateGateway extends Plugin<Channel> {
    EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command);

    EServiceTemplate getEServiceTemplate(EServiceTemplateRef templateRef, UUID versionId);

    EServiceTemplate linkOpenApiInterface(EServiceTemplateRef templateRef, UUID versionId, String openApiInterfacePath);

    EServiceTemplate publishVersion(EServiceTemplateRef templateRef, UUID versionId);

    EService instantiateEService(EServiceTemplateRef templateRef);
}

