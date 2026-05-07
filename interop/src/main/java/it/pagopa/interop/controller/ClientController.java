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

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientController {

    private final CurrentUserContext currentUserContext;
    private final ClientService clientService;

    @Given("un client {clientType} creato da {tenant}, associato alla {purposeCreated}, in cui è presente l'admin e una coppia di chiavi crittografiche")
    public void setupClient(InteropClientType clientType, Tenant consumer, Purpose purpose) {
        User consumerAdmin = User.getTenantAdmin(consumer);
        currentUserContext.set(consumerAdmin, consumer);

        Client client = clientService.createClient(
                clientType,
                request -> request.setMembers(List.of(consumerAdmin.getUserId()))
        );

        clientService.addPurpose(client, purpose);
    }
}
