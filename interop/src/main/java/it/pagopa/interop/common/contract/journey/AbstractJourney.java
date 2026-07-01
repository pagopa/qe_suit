package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import it.pagopa.interop.common.contract.model.shared.enums.UserRole;
import it.pagopa.interop.common.cucumber.context.UserContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractJourney<SELF extends AbstractJourney<SELF>> implements IUserJourney<SELF> {

    protected final UserContext userContext;

    protected Tenant producerTenant;
    protected User producerUser;
    protected Tenant consumerTenant;
    protected User consumerUser;

    @Override
    public SELF withProducer(Tenant tenant, User user) {
        this.producerTenant = tenant;
        this.producerUser = user;
        setUserContext(producerUser, producerTenant);
        return self();
    }

    @Override
    public SELF withConsumer(Tenant tenant, User user) {
        this.consumerTenant = tenant;
        this.consumerUser = user;
        setUserContext(consumerUser, consumerTenant);
        return self();
    }

    @Override
    public SELF withConsumer(Tenant tenant, UserRole role) {
        this.consumerTenant = tenant;
        this.consumerUser = User.getTenantUser(tenant, role);
        setUserContext(consumerUser, consumerTenant);
        return self();
    }

    @Override
    public SELF withProducer(Tenant tenant, UserRole role) {
        this.producerTenant = tenant;
        this.producerUser = User.getTenantUser(tenant, role);
        setUserContext(producerUser, producerTenant);
        return self();
    }

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    private void setUserContext(User user, Tenant tenant) {
        if (user == null || tenant == null) {
            return;
        }

        userContext.set(user, tenant);
    }
}