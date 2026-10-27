package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.application.BffEServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRestClient;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.EServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceGateway;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceDescriptorQuotas;
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
    public EService updateEServiceInstanceDescriptor(
            EServiceRef eServiceInstanceRef,
            EServiceDescriptorRef descriptorRef,
            EServiceDescriptorState currentDescriptorState,
            EServiceTemplateInstanceUpdateDescriptorCommand command
    ) {
        CreatedResource updated = switch (currentDescriptorState) {
            case DRAFT -> {
                UpdateEServiceDescriptorTemplateInstanceSeed draftInstancePayload = ((BffEServiceTemplateInstanceUpdateDescriptorCommand) command).getBffUpdateDraftServiceInstancePayload();
                yield restClient.updateDraftDescriptorTemplateInstance(eServiceInstanceRef.id(), descriptorRef.id(), draftInstancePayload)
                        .withPolling(PollingStrategy.UNTIL_SUCCESS)
                        .get();
            }
            case PUBLISHED -> {
                UpdateEServiceTemplateInstanceDescriptorQuotas publishedInstancePayload = ((BffEServiceTemplateInstanceUpdateDescriptorCommand) command).getBffUpdatePublishedEServiceInstancePayload();
                yield restClient.updateTemplateInstanceDescriptor(eServiceInstanceRef.id(), descriptorRef.id(), publishedInstancePayload)
                        .withPolling(PollingStrategy.UNTIL_SUCCESS)
                        .get();
            }
            default -> throw new IllegalArgumentException("Unsupported descriptor state: " + currentDescriptorState);
        };

        EServiceRef serviceRef = EServiceRef.of(updated.getId());
        descriptorGateway.getEServiceDescriptor(serviceRef, descriptorRef);
        return eServiceGateway.getEService(serviceRef);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}
