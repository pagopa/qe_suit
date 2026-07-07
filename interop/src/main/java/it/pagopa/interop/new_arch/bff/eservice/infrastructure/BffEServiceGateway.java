package it.pagopa.interop.new_arch.bff.eservice.infrastructure;

import it.pagopa.interop.new_arch.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.application.EServiceGateway;
import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.EServiceRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffEServiceGateway implements EServiceGateway {

    private final BffEServiceRestClient restClient;
    private final BffEServiceMapper mapper;

    @Override
    public EService createEService(EServiceCreationCommand command) {
        if (!(command instanceof BffEServiceCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceCreationCommand");

        return restClient.createEService(bffCommand.getBffCreationPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .saveToContext(createdEServiceDescriptor -> getEService(EServiceRef.of(createdEServiceDescriptor.getId())))
                .getModel();

    }

    @Override
    public EService getEService(EServiceRef eServiceRef) {
        return restClient.readEService(eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .saveToContext(mapper::toEService)
                .getModel();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return false;
    }
}
