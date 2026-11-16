package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.journey.application.UserJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UserChannelJourney implements UserJourney<UserChannelJourney> {

    private final AuthSession authSession;

    public UserChannelJourney(@Qualifier("userAuthSession") AuthSession authSession) {
        this.authSession = authSession;
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF || channel == Channel.WEB_BROWSER;
    }

    @Override
    public UserChannelJourney withProducer(Tenant tenant) {
        return withProducer(tenant, UserRole.ADMIN);
    }

    @Override
    public UserChannelJourney withConsumer(Tenant tenant) {
        return withConsumer(tenant, UserRole.ADMIN);
    }

    @Override
    public UserChannelJourney withProducer(Tenant tenant, InteropActor actor) {
        if (!(actor instanceof User)) {
            throw new IllegalArgumentException("BFF/WEB requires a user actor");
        }
        authSession.set(actor, tenant);
        return this;
    }

    @Override
    public UserChannelJourney withConsumer(Tenant tenant, InteropActor actor) {
        if (!(actor instanceof User)) {
            throw new IllegalArgumentException("BFF/WEB requires a user actor");
        }
        authSession.set(actor, tenant);
        return this;
    }

    @Override
    public UserChannelJourney withProducer(Tenant tenant, InteropRole role) {
        if (!(role instanceof UserRole)) {
            throw new IllegalArgumentException("BFF/WEB requires a user role");
        }
        authSession.set(role, tenant);
        return this;
    }

    @Override
    public UserChannelJourney withConsumer(Tenant tenant, InteropRole role) {
        if (!(role instanceof UserRole)) {
            throw new IllegalArgumentException("BFF/WEB requires a user role");
        }
        authSession.set(role, tenant);
        return this;
    }
}
