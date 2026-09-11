package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateRequestFactory extends Plugin<Channel> {
    EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand();

    UpdateEServiceTemplateVersionCommand defaultUpdateVersionCommand();
}

