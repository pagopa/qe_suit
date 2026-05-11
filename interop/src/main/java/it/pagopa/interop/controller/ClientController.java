package it.pagopa.interop.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.domain.services.client.ClientService;
import it.pagopa.interop.infrastructure.client.auth.context.user.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static it.pagopa.interop.domain.enums.InteropClientType.API;
import static it.pagopa.interop.domain.enums.InteropClientType.CONSUMER;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientController {

    private final CurrentUserContext currentUserContext;
    private final ClientService clientService;

    @Given("un client CONSUMER creato da {tenant}, associato alla {currentPurpose}, in cui è presente l'admin e una coppia di chiavi crittografiche")
    public void setupConsumerClient(Tenant consumer, Purpose purpose) {
        Client client = createClientWithAdmin(CONSUMER, consumer);
        clientService.addPurpose(client, purpose);
    }

    @Given("un client API creato da {tenant} in cui è presente l'admin e una coppia di chiavi crittografiche")
    public void setupApiClient(Tenant consumer) {
        createClientWithAdmin(API, consumer);
    }

    private Client createClientWithAdmin(InteropClientType type, Tenant tenant) {
        User admin = User.getTenantAdmin(tenant);
        currentUserContext.set(admin, tenant);

        Client client = clientService.createClient(
                type,
                request -> request.setMembers(List.of(admin.getUserId()))
        );
        clientService.addPublicKey(client);
        return client;
    }
}
