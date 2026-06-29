package it.pagopa.interop.bff.service;

import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.contract.model.client.ClientKind;
import it.pagopa.interop.common.contract.model.client.Client;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
import it.pagopa.interop.common.utils.KeyPairUtils;
import it.pagopa.interop.common.utils.PollingUtils;
import it.pagopa.interop.generated.openapi.clients.bff.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientService {

    private final ClientsApi clientsApi;
    private final ScenarioContext scenarioContext;

    public Client createClient(ClientKind kind) {
        return createClient(kind, null);
    }

    public Client createClient(ClientKind kind, Consumer<ClientSeed> overrides) {
        ClientSeed seed = buildDefaultSeed();
        Optional.ofNullable(overrides).ifPresent(o -> o.accept(seed));

        CreatedResource created = switch (kind) {
            case CONSUMER -> clientsApi.createConsumerClient(seed);
            case API -> clientsApi.createApiClient(seed);
        };

        return getClient(created.getId());
    }

    public Client getClient(UUID clientId) {
        Client client = pollClient(
                () -> new Client(clientsApi.getClient(clientId), new java.util.LinkedHashSet<>()),
                c -> c != null && Objects.equals(clientId, c.getId())
        );
        scenarioContext.upsert(client);
        return client;
    }

    public Client addPublicKey(Client client) {
        return addPublicKey(client, null, null);
    }

    public Client addPublicKey(Client client, Consumer<KeySeed> overrides) {
        return addPublicKey(client, null, overrides);
    }

    public Client addPublicKey(Client client, KeyPair keyPair) {
        return addPublicKey(client, keyPair, null);
    }

    public Client addPublicKey(Client client, KeyPair keyPair, Consumer<KeySeed> overrides) {
        KeyPair effectiveKeyPair = Optional.ofNullable(keyPair).orElseGet(() -> KeyPairUtils.generate(KeyPairUtils.KeyAlgorithm.RSA));
        KeySeed seed = buildKeySeed(effectiveKeyPair);
        Optional.ofNullable(overrides).ifPresent(o -> o.accept(seed));

        clientsApi.createKey(client.getId(), seed);

        pollClientKeys(client.getId(), seed.getName());

        client.addKeyPair(effectiveKeyPair);
        scenarioContext.upsert(client);

        return getClient(client.getId());
    }

    public Client addPurpose(Client client, Purpose purpose) {
        PurposeAdditionDetailsSeed seed = new PurposeAdditionDetailsSeed().purposeId(purpose.getId());
        clientsApi.addClientPurpose(client.getId(), seed);
        return getClient(client.getId());
    }

    private ClientSeed buildDefaultSeed() {
        return new ClientSeed()
                .name("client-" + ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE))
                .description("Default client description")
                .members(java.util.List.of());
    }

    private KeySeed buildKeySeed(KeyPair keyPair) {
        String alg = resolveJwtAlg(keyPair);
        return new KeySeed()
                .name("key-" + ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE))
                .use(it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse.SIG)
                .alg(alg)
                .key(KeyPairUtils.toBase64Pem(keyPair.getPublic()));
    }

    private String resolveJwtAlg(KeyPair keyPair) {
        return switch (keyPair.getPublic().getAlgorithm()) {
            case "RSA" -> "RS256";
            case "EC" -> "ES256";
            case "Ed25519", "EdDSA" -> "EdDSA";
            default ->
                    throw new IllegalArgumentException("Unsupported key algorithm: " + keyPair.getPublic().getAlgorithm());
        };
    }

    private Client pollClient(Supplier<Client> supplier, Predicate<Client> predicate) {
        return PollingUtils.pollUntil(
                supplier,
                predicate,
                Duration.ofSeconds(20),
                Duration.ofSeconds(2)
        );
    }

    private void pollClientKeys(UUID clientId, String keyName) {
        PollingUtils.pollUntil(
                () -> clientsApi.getClientKeys(clientId, 0, 50, null),
                resp -> resp != null && resp.getKeys() != null && resp.getKeys().stream().anyMatch(k -> Objects.equals(k.getName(), keyName)),
                Duration.ofSeconds(20),
                Duration.ofSeconds(2)
        );
    }
}