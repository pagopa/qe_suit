package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorQuotas;
import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.infrastructure.context.EntityStore;
import it.pagopa.interop.common.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BffEServiceDescriptorGateway implements EServiceDescriptorGateway {

    private final BffEServiceRestClient restClient;
    private final EntityStore entityStore;
    private final BffEServiceDescriptorMapper mapper;

    @Override
    public EServiceDescriptor getEServiceDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        return restClient.readDescriptor(eServiceRef.id(), descriptorRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(descriptor -> {
                    Optional<EService> maybeEService = entityStore.getById(eServiceRef.id(), EService.class);
                    return mapper.toEServiceWithUpsert(descriptor, maybeEService.orElse(null));
                })
                .updateContext()
                .map(eService -> eService.findDescriptor(descriptorRef.id()))
                .get();
    }

    @Override
    public EServiceDescriptor publishDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        return restClient.publishDescriptor(eServiceRef.id(), descriptorRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(emptyResp -> getEServiceDescriptor(eServiceRef, descriptorRef))
                .get();
    }

    @Override
    public EServiceDescriptor updateDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, UpdateEServiceDescriptorCommand command) {
        if (!(command instanceof BffUpdateEServiceDescriptorCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffUpdateEServiceDescriptorCommand");

        UpdateEServiceDescriptorSeed payload = bffCommand.getBffPayload();

        return restClient.updateDescriptor(eServiceRef.id(), descriptorRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getEServiceDescriptor(eServiceRef, descriptorRef))
                .get();
    }

    @Override
    public EServiceDescriptor linkOpenApiInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, File openApiInterface) {
        return restClient.addDocument(eServiceRef.id(), descriptorRef.id(), "INTERFACE", openApiInterface.getName(), openApiInterface)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getEServiceDescriptor(eServiceRef, descriptorRef))
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
