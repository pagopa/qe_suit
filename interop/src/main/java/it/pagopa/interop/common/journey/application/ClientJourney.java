package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.kernel.domain.Key;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;

import java.util.UUID;
import java.util.function.Consumer;

public interface ClientJourney<SELF extends ClientJourney<SELF>> extends JourneyModule {
    SELF createClient(Consumer<ClientCreationCommand> creationCommand);

    SELF linkPurposeToClient(Purpose... purposeIds);
}
