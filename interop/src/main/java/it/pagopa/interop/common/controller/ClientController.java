package it.pagopa.interop.common.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.bff.service.ClientService;
import it.pagopa.interop.common.contract.enums.InteropClientType;
import it.pagopa.interop.common.contract.enums.Tenant;
import it.pagopa.interop.common.contract.enums.User;
import it.pagopa.interop.common.contract.model.client.Client;
import it.pagopa.interop.common.contract.model.purpose.Purpose;

import it.pagopa.interop.common.cucumber.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static it.pagopa.interop.common.contract.enums.InteropClientType.API;
import static it.pagopa.interop.common.contract.enums.InteropClientType.CONSUMER;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientController {

    private final UserContext userContext;
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
        userContext.set(admin, tenant);

        Client client = clientService.createClient(
                type,
                request -> request.setMembers(List.of(admin.getUserId()))
        );
        clientService.addPublicKey(client);
        return client;
    }
}
