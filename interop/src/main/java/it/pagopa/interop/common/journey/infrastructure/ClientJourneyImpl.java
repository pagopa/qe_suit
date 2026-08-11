package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.client.application.ClientUseCase;
import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.context.EntityStore;
import it.pagopa.interop.common.journey.application.ClientJourney;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ClientJourneyImpl implements ClientJourney<ClientJourneyImpl> {

    private final ClientUseCase clientUseCase;
    private final EntityStore entityStore;

    public ClientJourneyImpl createClient(Consumer<ClientCreationCommand> creationCommand) {
        clientUseCase.createClient(creationCommand);
        return this;
    }

    @Override
    public ClientJourneyImpl linkPurposeToClient(Purpose... purposeIds) {
        Client client = entityStore.getLastOrThrow(Client.class);

        Arrays.stream(purposeIds)
                .forEach(purpose ->
                        clientUseCase.linkPurpose(client, purpose)
                );

        return this;
    }
}
