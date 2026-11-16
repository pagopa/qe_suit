package it.pagopa.interop.common.client.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.client.application.ClientUseCase;
import it.pagopa.interop.common.client.domain.Client;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientSteps {
    private final ClientUseCase clientUseCase;

    @When("l'utente tenta di eliminare la chiave del {currentClient}")
    public void attemptClientKeyDeletion(Client client) {
        clientUseCase.attemptClientKeyDeletion(client);
    }
}
