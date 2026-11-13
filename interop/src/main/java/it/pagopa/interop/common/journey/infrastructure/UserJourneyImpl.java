package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.journey.application.UserJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserJourneyImpl implements UserJourney<UserJourneyImpl> {

    private final AuthSession authSession;

    @Override
    public UserJourneyImpl withProducer(Tenant tenant, User user) {
        authSession.set(user, tenant);
        return this;
    }

    @Override
    public UserJourneyImpl withConsumer(Tenant tenant, User user) {
        authSession.set(user, tenant);
        return this;
    }

    @Override
    public UserJourneyImpl withProducer(Tenant tenant, UserRole role) {
        authSession.set(User.getTenantUser(tenant, role), tenant);
        return this;
    }

    @Override
    public UserJourneyImpl withConsumer(Tenant tenant, UserRole role) {
        authSession.set(User.getTenantUser(tenant, role), tenant);
        return this;
    }
}
