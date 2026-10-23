package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateInstanceGateway extends Plugin<Channel> {
    EService createEServiceInstance(EServiceTemplate template);

    EServiceDescriptor updateDescriptor(EService instance, EServiceDescriptor descriptor);
}
