package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import org.springframework.plugin.core.Plugin;

import java.util.Optional;

public interface EServiceTemplateGateway extends Plugin<Channel> {
    EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command);

    EServiceTemplate uploadInterface(EServiceTemplate template, EServiceTemplateVersion version, String fixturePath);

    EServiceTemplate publishEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version);

    EServiceTemplate getEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version);

    EServiceTemplateInterface downloadInterface(EServiceTemplate template, EServiceTemplateVersion version);

    Optional<EServiceTemplateInterface> readCurrentInterface(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef
    );
}
