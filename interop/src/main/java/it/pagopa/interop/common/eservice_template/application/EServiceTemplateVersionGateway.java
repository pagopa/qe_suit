package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import org.springframework.plugin.core.Plugin;

import java.util.Optional;

public interface EServiceTemplateVersionGateway extends Plugin<Channel> {
    EServiceTemplateVersion getEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    EServiceTemplateVersion addVersion(EServiceTemplateRef templateRef);

    EServiceTemplateVersion publishEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    EServiceTemplateVersion uploadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef, String fixturePath);

    EServiceTemplateVersion updateDraftVersion(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            UpdateEServiceTemplateVersionCommand command
    );

    EServiceTemplateInterface downloadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    Optional<EServiceTemplateInterface> readCurrentInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);
}

