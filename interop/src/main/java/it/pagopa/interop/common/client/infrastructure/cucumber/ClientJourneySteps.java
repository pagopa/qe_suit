package it.pagopa.interop.common.client.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ClientJourneySteps {
    private final InteropJourney interopJourney;

    @Given("un client CONSUMER creato da/dal {tenant}, associato alla {currentPurpose}, in cui è presente un (utente ){userRole} e una coppia di chiavi crittografiche")
    public void setupConsumerClient(Tenant consumer, Purpose purpose, UserRole userRole) {
        interopJourney
                .withConsumer(consumer)
                .createClient(clientConfig ->
                        clientConfig
                                .name(RandomUtils.randomAlphanumericName("client"))
                                .kind(ClientKind.CONSUMER)
                                .users(UserRef.of(User.getTenantUser(consumer, userRole), consumer))
                                .keys(List.of(ClientKeyCreationCommand::randomClientKey))
                )
                .linkPurposeToClient(purpose);
    }

    @Given("un client API creato da/dal {tenant} in cui è presente un (utente ){userRole} e una coppia di chiavi crittografiche")
    public void setupApiClient(Tenant consumer, UserRole userRole) {
        interopJourney
                .withConsumer(consumer)
                .createClient(clientConfig ->
                        clientConfig
                                .name(RandomUtils.randomAlphanumericName("client"))
                                .kind(ClientKind.API)
                                .users(UserRef.of(User.getTenantUser(consumer, userRole), consumer))
                                .keys(List.of(ClientKeyCreationCommand::randomClientKey))
                );
    }

    @Given("un client {string} CONSUMER creato da/dal {tenant}, associato alla {currentPurpose}, in cui è presente un (utente ){userRole} e una coppia di chiavi crittografiche")
    public void setupConsumerClient(String alias, Tenant consumer, Purpose purpose, UserRole userRole) {
        interopJourney
            .withConsumer(consumer)
            .createClient(clientConfig ->
                clientConfig
                    .name(RandomUtils.randomAlphanumericName("client"))
                    .kind(ClientKind.CONSUMER)
                    .users(UserRef.of(User.getTenantUser(consumer, userRole), consumer))
                    .keys(List.of(ClientKeyCreationCommand::randomClientKey))
                    .alias(alias)
            )
            .linkPurposeToClient(purpose);
    }

    @Given("un client {clientType} creato da/dal {tenant} in cui è presente un (utente ){userRole} e una coppia di chiavi crittografiche")
    public void setupClient(ClientKind kind, Tenant consumer, UserRole userRole) {
        if (userRole == UserRole.ADMIN) {
            interopJourney.withConsumer(consumer);
        } else {
            interopJourney.withConsumer(consumer, userRole);
        }
        interopJourney
            .createClient(clientConfig ->
                clientConfig
                    .name(RandomUtils.randomAlphanumericName("client"))
                    .kind(kind)
                    .users(UserRef.of(User.getTenantUser(consumer, userRole), consumer))
                    .keys(List.of(ClientKeyCreationCommand::randomClientKey))
            );
    }

    @Given("un client {string} {clientType} creato da/dal {tenant} in cui è presente un (utente ){userRole} e una coppia di chiavi crittografiche")
    public void setupClient(String alias, ClientKind kind, Tenant consumer, UserRole userRole) {
        if (userRole == UserRole.ADMIN) {
            interopJourney.withConsumer(consumer);
        } else {
            interopJourney.withConsumer(consumer, userRole);
        }
        interopJourney
            .createClient(clientConfig ->
                clientConfig
                    .name(RandomUtils.randomAlphanumericName("client"))
                    .kind(kind)
                    .users(UserRef.of(User.getTenantUser(consumer, userRole), consumer))
                    .keys(List.of(ClientKeyCreationCommand::randomClientKey))
                    .alias(alias)
            );
    }

    @Given("un client CONSUMER creato da/dal {tenant} senza finalità associate")
    public void setupConsumerClientWithoutPurposes(Tenant consumer) {
        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .createClient(command -> command.name(RandomUtils.randomAlphanumericName("client"))
                        .kind(ClientKind.CONSUMER)
                        .users(UserRef.of(User.getTenantAdmin(consumer), consumer)));
    }
}
