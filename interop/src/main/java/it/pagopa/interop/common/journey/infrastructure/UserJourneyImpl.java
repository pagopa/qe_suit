package it.pagopa.interop.new_arch.common.journey.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.UserContext;
import it.pagopa.interop.new_arch.common.journey.application.UserJourney;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import it.pagopa.interop.new_arch.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ScenarioScope
public class UserJourneyImpl implements UserJourney<UserJourneyImpl> {

    private final UserContext userContext;

    private Tenant producerTenant;
    private User producerUser;
    private Tenant consumerTenant;
    private User consumerUser;

    @Override
    public UserJourneyImpl withProducer(Tenant tenant, User user) {
        this.producerTenant = tenant;
        this.producerUser = user;
        userContext.set(producerUser, producerTenant);
        return this;
    }

    @Override
    public UserJourneyImpl withConsumer(Tenant tenant, User user) {
        this.consumerTenant = tenant;
        this.consumerUser = user;
        userContext.set(consumerUser, consumerTenant);
        return this;
    }

    @Override
    public UserJourneyImpl withProducer(Tenant tenant, UserRole role) {
        this.producerTenant = tenant;
        this.producerUser = User.getTenantUser(tenant, role);
        userContext.set(producerUser, producerTenant);
        return this;
    }

    @Override
    public UserJourneyImpl withConsumer(Tenant tenant, UserRole role) {
        this.consumerTenant = tenant;
        this.consumerUser = User.getTenantUser(tenant, role);
        userContext.set(consumerUser, consumerTenant);
        return this;
    }
}
