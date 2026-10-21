package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateInstanceGateway extends Plugin<Channel> {
    EService createEServiceInstance(EServiceTemplate template, EServiceTemplateInstanceCreationCommand command);
}
