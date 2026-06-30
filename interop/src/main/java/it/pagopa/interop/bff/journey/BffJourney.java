package it.pagopa.interop.bff.journey;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.bff.service.EServiceBffService;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.contract.template.journey.Journey;
import it.pagopa.interop.common.cucumber.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@ScenarioScope
@RequiredArgsConstructor
public class BffJourney implements Journey {

    private final UserContext userContext;
    private final EServiceBffService eServiceBffService;

    private Tenant producerTenant;
    private User producerUser;
    private Tenant consumerTenant;
    private User consumerUser;

    @Override
    public Journey withProducer(Tenant tenant, User user) {
        this.producerTenant = tenant;
        this.producerUser = user;
        setUserContext(user, tenant);
        return this;
    }

    @Override
    public Journey withConsumer(Tenant tenant, User user) {
        this.consumerTenant = tenant;
        this.consumerUser = user;
        setUserContext(user, tenant);
        return this;
    }

    @Override
    public Journey createDraftEService() {
        eServiceBffService.create()
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext();
        return this;
    }

    @Override
    public Journey publishEservice() {
        // TODO: implementare la pubblicazione dell'e-service
        return this;
    }

    private void setUserContext(User user, Tenant tenant) {
        if (user == null || tenant == null) {
            return;
        }

        userContext.set(user, tenant);
    }
}