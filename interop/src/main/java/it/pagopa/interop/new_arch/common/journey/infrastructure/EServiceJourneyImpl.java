package it.pagopa.interop.new_arch.common.journey.infrastructure;

import it.pagopa.interop.new_arch.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.journey.application.EServiceJourney;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class EServiceJourneyImpl implements EServiceJourney<EServiceJourneyImpl> {
    private final EServiceUseCase eServiceUseCase;
    private final EServiceDescriptorUseCase  eServiceDescriptorUseCase;
    private final DomainContext domainContext;

    @Override
    public EServiceJourneyImpl createEService(EServiceCreationCommand command, EServiceDescriptorState state) {
        // DRAFT
        EService draftEService = eServiceUseCase.createEService(command);
        if (state == EServiceDescriptorState.DRAFT) return this;

        // PUBLISHED
        if(state != EServiceDescriptorState.PUBLISHED)
            throw new UnsupportedOperationException("State " + state + " not supported yet.");



        return null;
    }

    @Override
    public EServiceJourneyImpl createEService(Consumer<EServiceCreationCommand> command, EServiceDescriptorState state) {
        return null;
    }

    @Override
    public EServiceJourneyImpl createEService(EServiceDescriptorState state) {
        return null;
    }

//    @Override
//    public EServiceJourneyImpl createEService(EServiceCreationCommand command) {
//        eServiceUseCase.createEService(command);
//        return this;
//    }
//
//    @Override
//    public EServiceJourneyImpl createEService(Consumer<EServiceCreationCommand> command) {
//        eServiceUseCase.createEService(command);
//        return this;
//    }
//
//    @Override
//    public EServiceJourneyImpl createEService() {
//        eServiceUseCase.createEService(cmd -> {});
//        return this;
//    }
}
