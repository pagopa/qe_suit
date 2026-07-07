package it.pagopa.interop.new_arch.common.eservice.application;

import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.kernel.domain.EServiceRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EServiceUseCase {
    private final EServiceGateway gateway;
    private final EServiceRequestFactory requestFactory;

    public EService createEService(Consumer<EServiceCreationCommand> config){
        EServiceCreationCommand creationCommand = requestFactory.createDefaultCreationRequest();
        config.accept(creationCommand);

        return createEService(creationCommand);
    }

   public EService createEService(EServiceCreationCommand creationCommand){
       return gateway.createEService(creationCommand);
   }

   public EService getEService(EServiceRef eServiceRef){
       return gateway.getEService(eServiceRef);
   }
}
