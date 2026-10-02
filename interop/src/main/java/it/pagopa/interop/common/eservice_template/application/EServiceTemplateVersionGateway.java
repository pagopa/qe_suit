package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateVersionGateway extends Plugin<Channel> {
    EServiceTemplateVersion getEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    EServiceTemplateVersion addVersion(EServiceTemplateRef templateRef);

    EServiceTemplateVersion publishVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    EServiceTemplateVersion updateDraftVersion(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            UpdateEServiceTemplateVersionCommand command
    );

    EServiceTemplateVersion linkOpenApiInterface(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            String openApiInterfacePath
    );
}

