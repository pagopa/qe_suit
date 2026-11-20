package it.pagopa.interop.common.client.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.client.application.ClientUseCase;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.kernel.domain.KeyRegistrationMaterial;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientSteps {
    private final ClientUseCase clientUseCase;

    @When("l'utente tenta di eliminare la chiave del {currentClient}")
    public void attemptClientKeyDeletion(Client client) {
        clientUseCase.attemptClientKeyDeletion(client);
    }

    @When("l'utente tenta di creare un client \"{clientType}\" con il nome {string}")
    public void attemptClientCreation(ClientKind kind, String name) {
        clientUseCase.attemptClientCreation(kind, name);
    }

    @When("l'erogatore tenta di aggiungere la chiave di test \"{keyRegistrationMaterial}\" al \"{currentClient}\" con il nome {string}")
    public void attemptKeyRegistration(KeyRegistrationMaterial material, Client client, String name) {
        clientUseCase.attemptKeyRegistration(client, material, name);
    }

    @When("il fruitore tenta di associare il {currentClient} alla {currentPurpose}")
    public void attemptPurposeAssociation(Client client, Purpose purpose) {
        clientUseCase.attemptPurposeAssociation(client, purpose);
    }
}
