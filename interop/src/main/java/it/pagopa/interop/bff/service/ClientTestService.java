package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.service.mapper.ClientMapper;
import it.pagopa.interop.bff.support.ClientSeedFactory;
import it.pagopa.interop.common.contract.model.client.Client;
import it.pagopa.interop.common.contract.model.client.ClientKind;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import it.pagopa.interop.common.contract.model.shared.enums.UserRole;
import it.pagopa.interop.common.contract.service.IClientTestService;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.generated.openapi.clients.bff.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientTestService extends RestService implements IClientTestService {

    private final ClientSeedFactory seedFactory;
    private final ScenarioContext scenarioContext;
    private final ClientsApi clientsApi;
    private final ClientMapper mapper;

    @Override
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Client, Client> read(UUID clientId) {
        Optional<Client> current = scenarioContext.getById(clientId, Client.class);

        return super.read(
                () -> clientsApi.getClientWithHttpInfo(clientId),
                (source) -> mapper.toDomainWithUpsert(source, current.orElse(null))
        );
    }

    @Override
    public TestChain<?, Client> createClientIncludingUsers(ClientKind kind, Tenant tenant, UserRole... roles) {
        List<UUID> members = new ArrayList<>();

        for (UserRole role : roles)
            members.add(User.getTenantUser(tenant, role).getUserId());

        return null;
    }

    @Override
    public TestChain<CreatedResource, Client> create(ClientKind kind, List<UUID> members) {
        ClientSeed seed = seedFactory.fullCreationRequest(members);

        return super.create(
                () -> switch (kind) {
                    case CONSUMER -> clientsApi.createConsumerClientWithHttpInfo(seed);
                    case API -> clientsApi.createApiClientWithHttpInfo(seed);
                },
                (res) -> readClientAndUpsert(res.getId())
        );
    }

    @Override
    public TestChain<Void, Client> addKey(UUID clientId, KeyPair key) {
        KeySeed seed = seedFactory.fullKeyCreationRequest(key);

        return super.create(
                () -> clientsApi.createKeyWithHttpInfo(clientId, seed),
                (Void) -> readClientAndUpsert(clientId)
        );
    }

    @Override
    public TestChain<Void, Client> addPurpose(UUID clientId, UUID purposeId) {
        PurposeAdditionDetailsSeed seed = seedFactory.fullPurposeAdditionRequest(purposeId);
        return super.create(
                () -> clientsApi.addClientPurposeWithHttpInfo(clientId, seed),
                (Void) -> readClientAndUpsert(clientId)
        );
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private Client readClientAndUpsert(UUID clientId) {
        return read(clientId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .getModel();
    }

}
