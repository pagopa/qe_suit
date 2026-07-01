package it.pagopa.interop.common.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.bff.journey.TestJourney;
import it.pagopa.interop.common.contract.model.client.ClientKind;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientController {

    private final TestJourney journey;

    @Given("un client CONSUMER creato da {tenant}, associato alla {currentPurpose}, in cui è presente l'admin e una coppia di chiavi crittografiche")
    public void setupConsumerClient(Tenant consumer, Purpose purpose) {
        journey
            .withConsumer(consumer, UserRole.ADMIN)
            .createClientAndInclude(ClientKind.CONSUMER, UserRole.ADMIN)
            .generateKeyAndLinkToClient()
            .linkPurposeToClient(purpose.getId());
    }

    @Given("un client API creato da {tenant} in cui è presente l'admin e una coppia di chiavi crittografiche")
    public void setupApiClient(Tenant consumer) {
        journey
            .withConsumer(consumer, UserRole.ADMIN)
            .createClientAndInclude(ClientKind.API, UserRole.ADMIN)
            .generateKeyAndLinkToClient();
    }
}
