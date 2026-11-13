package it.pagopa.interop.bff.client.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.client.application.ClientUseCase;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BffClientSteps {

    private final EntityStore entityStore;
    private final ClientUseCase clientUseCase;

    @Given("il client {string} associato alla finalità")
    public void linkClientToPurpose(String clientAlias) {
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);

        clientUseCase.linkPurpose(client, purpose);
    }
}
