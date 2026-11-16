package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.journey.application.UserJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.InteropActor;
import it.pagopa.interop.common.kernel.domain.InteropRole;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserJourneyImpl implements UserJourney<UserJourneyImpl> {

    private final AuthSession authSession;

    @Override
    public UserJourneyImpl withProducer(Tenant tenant) {
        authSession.set(tenant);
        return this;
    }

    @Override
    public UserJourneyImpl withConsumer(Tenant tenant) {
        authSession.set(tenant);
        return this;
    }

    @Override
    public UserJourneyImpl withProducer(Tenant tenant, InteropActor actor) {
        authSession.set(actor, tenant);
        return this;
    }

    @Override
    public UserJourneyImpl withConsumer(Tenant tenant, InteropActor actor) {
        authSession.set(actor, tenant);
        return this;
    }

    @Override
    public UserJourneyImpl withProducer(Tenant tenant, InteropRole role) {
        authSession.set(role, tenant);
        return this;
    }

    @Override
    public UserJourneyImpl withConsumer(Tenant tenant, InteropRole role) {
        authSession.set(role, tenant);
        return this;
    }
}
