package it.pagopa.interop.new_arch.common.journey.application;

import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;

import java.util.function.Consumer;

public interface EServiceJourney<SELF extends EServiceJourney<SELF>> extends JourneyModule{
    SELF createEService(EServiceCreationCommand command);
    SELF createEService(Consumer<EServiceCreationCommand> command);
    SELF createEService();
}
