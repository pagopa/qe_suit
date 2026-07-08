package it.pagopa.interop.new_arch.bff.eservice.infrastructure;

import it.pagopa.interop.new_arch.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.application.EServiceGateway;
import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.new_arch.common.kernel.domain.EServiceRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BffEServiceGateway implements EServiceGateway {

    private final BffEServiceRestClient restClient;
    private final BffEServiceDescriptorGateway descriptorGateway;
    private final DomainContext domainContext;
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
                .get();
    }

    @Override
    public EService getEService(EServiceRef eServiceRef) {
        return restClient.readEService(eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(eServiceDetails -> {
                    Optional<EService> maybeEService = domainContext.getById(eServiceRef.id(), EService.class);
                    return mapper.toEServicePreservingDescriptors(eServiceDetails, maybeEService.orElse(null));
                })
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
