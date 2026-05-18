package it.pagopa.interop.domain.services.client;

import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;

import java.security.KeyPair;
import java.util.UUID;
import java.util.function.Consumer;

public interface ClientService {
    Client createClient(InteropClientType kind);
    Client createClient(InteropClientType kind, Consumer<ClientSeed> overrides);
    Client getClient(UUID clientId);
    Client addPublicKey(Client client, KeyPair keyPair);
    Client addPublicKey(Client client, KeyPair keyPair, Consumer<KeySeed> overrides);
    Client addPublicKey(Client client);
    Client addPublicKey(Client client, Consumer<KeySeed> overrides);
    Client addPurpose(Client client, Purpose purpose);
}