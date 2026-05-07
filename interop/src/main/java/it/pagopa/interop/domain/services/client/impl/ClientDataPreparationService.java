package it.pagopa.interop.domain.services.client.impl;

import it.pagopa.interop.domain.context.ClientContext;
import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.domain.services.client.ClientService;
import it.pagopa.interop.generated.openapi.clients.bff.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.utils.KeyPairUtils;
import it.pagopa.interop.utils.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientDataPreparationService implements ClientService {

    private final ClientsApi clientsApi;
    private final ClientContext context;

    @Override
    public Client createClient(InteropClientType kind) {
        return createClient(kind, null);
    }

    @Override
    public Client createClient(InteropClientType kind, Consumer<ClientSeed> overrides) {
        ClientSeed seed = buildDefaultSeed();
        if (overrides != null) {
            overrides.accept(seed);
        }

        CreatedResource created = switch (kind) {
            case CONSUMER -> clientsApi.createConsumerClient(seed);
            case API -> clientsApi.createApiClient(seed);
        };

        return getClient(created.getId());
    }

    @Override
    public Client getClient(UUID clientId) {
        Client client = PollingUtils.pollUntil(
                () -> new Client(clientsApi.getClient(clientId), new java.util.LinkedHashSet<>()),
                resp -> resp != null && Objects.equals(clientId, resp.getId()),
                Duration.ofSeconds(20),
                Duration.ofSeconds(2)
        );

        context.upsert(client);
        return client;
    }

    @Override
    public Client addPublicKey(Client client) {
        return addPublicKey(client, null, null);
    }

    @Override
    public Client addPublicKey(Client client, Consumer<KeySeed> overrides) {
        return addPublicKey(client, null, overrides);
    }

    @Override
    public Client addPurpose(Client client, Purpose purpose) {
        PurposeAdditionDetailsSeed seed = new PurposeAdditionDetailsSeed().purposeId(purpose.getId());
        clientsApi.addClientPurpose(client.getId(), seed);
        return getClient(client.getId());
    }

    @Override
    public Client addPublicKey(Client client, KeyPair keyPair) {
        return addPublicKey(client, keyPair, null);
    }

    @Override
    public Client addPublicKey(Client client, KeyPair keyPair, Consumer<KeySeed> overrides) {
        KeyPair effectiveKeyPair = keyPair != null ? keyPair : KeyPairUtils.generate(KeyPairUtils.KeyAlgorithm.RSA);
        KeySeed seed = buildKeySeed(effectiveKeyPair);

        if (overrides != null) {
            overrides.accept(seed);
        }

        clientsApi.createKey(client.getId(), seed);

        PollingUtils.pollUntil(() -> clientsApi.getClientKeys(client.getId(), 0, 50, null), resp -> resp != null && resp.getKeys() != null && resp.getKeys().stream().anyMatch(k -> Objects.equals(k.getName(), seed.getName())), Duration.ofSeconds(20), Duration.ofSeconds(2));

        client.addKeyPair(effectiveKeyPair);
        context.upsert(client);

        return getClient(client.getId());
    }

    private ClientSeed buildDefaultSeed() {
        return new ClientSeed().name("client-" + ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)).description("Default client description").members(java.util.List.of());
    }

    private KeySeed buildKeySeed(KeyPair keyPair) {
        String alg = resolveJwtAlg(keyPair);

        return new KeySeed().name("key-" + ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)).use(it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse.SIG).alg(alg).key(KeyPairUtils.toBase64Pem(keyPair.getPublic()));
    }

    private String resolveJwtAlg(KeyPair keyPair) {
        String keyAlg = keyPair.getPublic().getAlgorithm();

        return switch (keyAlg) {
            case "RSA" -> "RS256";
            case "EC" -> "ES256";
            case "Ed25519", "EdDSA" -> "EdDSA";
            default -> throw new IllegalArgumentException("Unsupported key algorithm: " + keyAlg);
        };
    }
}