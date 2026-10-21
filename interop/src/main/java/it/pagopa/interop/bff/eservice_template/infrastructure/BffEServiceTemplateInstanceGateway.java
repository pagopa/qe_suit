package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRestClient;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceGateway;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateInstanceCreationCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateInstanceGateway implements EServiceTemplateInstanceGateway {
    private final BffEServiceRestClient restClient;
    private final BffEServiceGateway eServiceGateway;
    private final BffEServiceDescriptorGateway descriptorGateway;

    @Override
    public EService createEServiceInstance(EServiceTemplate template, EServiceTemplateInstanceCreationCommand command) {
        if (!(command instanceof BffEServiceTemplateInstanceCreationCommand bffCommand)) {
            throw new IllegalArgumentException("Expected BffEServiceTemplateInstanceCreationCommand");
        }
        var created = restClient.createEServiceInstanceFromTemplate(
                        template.getId(), bffCommand.getBffPayload()
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        EServiceRef serviceRef = EServiceRef.of(created.getId());
        EServiceDescriptorRef descriptorRef = EServiceDescriptorRef.of(created.getDescriptorId());

        descriptorGateway.getEServiceDescriptor(serviceRef, descriptorRef);
        return eServiceGateway.getEService(serviceRef);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}
