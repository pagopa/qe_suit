package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.kernel.utils.RandomUtils;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ClientJourneySteps {
    private final InteropJourney interopJourney;

    @Given("un client CONSUMER creato da/dal {tenant}, associato alla {currentPurpose}, in cui è presente un (utente ){userRole} e una coppia di chiavi crittografiche")
    public void setupConsumerClient(Tenant consumer, Purpose purpose, UserRole userRole) {
        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .createClient(clientConfig ->
                        clientConfig
                                .name(RandomUtils.randomAlphanumericName("client"))
                                .kind(ClientKind.CONSUMER)
                                .users(UserRef.of(User.getTenantUser(consumer, userRole), consumer))
                                .keys(List.of(ClientKeyCreationCommand::randomClientConsumerKey))
                )
                .linkPurposeToClient(purpose);
    }

    @Given("un client API creato da/dal {tenant} in cui è presente un (utente ){userRole} e una coppia di chiavi crittografiche")
    public void setupApiClient(Tenant consumer, UserRole userRole) {
        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .createClient(clientConfig ->
                        clientConfig
                                .name(RandomUtils.randomAlphanumericName("client"))
                                .kind(ClientKind.API)
                                .users(UserRef.of(User.getTenantUser(consumer, userRole), consumer))
                                .keys(List.of(ClientKeyCreationCommand::randomClientConsumerKey))
                );
    }
}
