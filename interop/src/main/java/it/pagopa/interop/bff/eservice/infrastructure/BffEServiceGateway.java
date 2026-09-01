package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BffEServiceGateway implements EServiceGateway {

    private final BffEServiceRestClient restClient;
    private final BffEServiceDescriptorGateway descriptorGateway;
    private final EntityStore entityStore;
    private final BffEServiceMapper mapper;

    @Override
    public EService createEService(EServiceCreationCommand command) {
        if (!(command instanceof BffEServiceCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceCreationCommand");

        return restClient.createEService(bffCommand.getBffCreationPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdEServiceDescriptor -> {
                    final EServiceRef eServiceRef = EServiceRef.of(createdEServiceDescriptor.getId());
                    final EServiceDescriptorRef descriptorRef = EServiceDescriptorRef.of(createdEServiceDescriptor.getDescriptorId());

                    final EServiceDescriptor createdDescriptor = descriptorGateway.getEServiceDescriptor(eServiceRef, descriptorRef);
                    final EService savedEService = getEService(eServiceRef);
                    savedEService.addDescriptor(createdDescriptor);

                    return savedEService;
                })
                .updateContext()
                .get();
    }

    @Override
    public EService getEService(EServiceRef eServiceRef) {
        return restClient.readEService(eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(eServiceDetails -> {
                    Optional<EService> maybeEService = entityStore.getById(eServiceRef.id(), EService.class);
                    return mapper.toEServicePreservingDescriptors(eServiceDetails, maybeEService.orElse(null));
                })
                .updateContext()
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
