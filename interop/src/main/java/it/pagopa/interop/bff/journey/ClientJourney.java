package it.pagopa.interop.bff.journey;

import it.pagopa.interop.bff.service.ClientTestService;
import it.pagopa.interop.common.contract.journey.IClientJourney;
import it.pagopa.interop.common.contract.model.client.Client;
import it.pagopa.interop.common.contract.model.client.ClientKind;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.UserRole;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.cucumber.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientJourney implements IClientJourney<ClientJourney> {

    private final UserContext userContext;
    private final ScenarioContext scenarioContext;
    private final ClientTestService clientTestService;

    @Override
    public ClientJourney createClient(ClientKind kind) {
        clientTestService.create(kind)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext();

        return this;
    }

    @Override
    public ClientJourney createClientAndInclude(ClientKind kind, UserRole... roles) {
        Tenant currentTenant = userContext.getTenant();

        clientTestService.createClientIncludingUsers(kind, currentTenant, roles)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext();

        return this;
    }

    @Override
    public ClientJourney linkPurposeToClient() {
        Purpose lastPurpose = scenarioContext.getLastOrThrow(Purpose.class);
        Client lastClient = scenarioContext.getLastOrThrow(Client.class);

        clientTestService.addPurpose(lastClient.getId(), lastPurpose.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext();

        return this;
    }

    @Override
    public ClientJourney linkPurposeToClient(UUID purposeId) {
        Client lastClient = scenarioContext.getLastOrThrow(Client.class);

        clientTestService.addPurpose(lastClient.getId(), purposeId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext();

        return this;
    }

    @Override
    public ClientJourney generateKeyAndLinkToClient() {
        Client lastClient = scenarioContext.getLastOrThrow(Client.class);

        clientTestService.addKey(lastClient.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext();

        return this;
    }
}
