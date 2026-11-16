package it.pagopa.interop.m2m.v3.infrastructure.journey;

import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.journey.application.UserJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.InteropActor;
import it.pagopa.interop.common.kernel.domain.InteropRole;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("m2Mv3UserJourney")
public class M2Mv3UserJourney implements UserJourney<M2Mv3UserJourney> {
    private final AuthSession authSession;

    public M2Mv3UserJourney(@Qualifier("m2mV3AuthSession") AuthSession authSession) {
        this.authSession = authSession;
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.M2M_V3;
    }

    @Override
    public M2Mv3UserJourney withProducer(Tenant tenant) {
        return withProducer(tenant, M2MRole.M2M_ADMIN);
    }

    @Override
    public M2Mv3UserJourney withConsumer(Tenant tenant) {
        return withConsumer(tenant, M2MRole.M2M_ADMIN);
    }

    @Override
    public M2Mv3UserJourney withProducer(Tenant tenant, InteropActor actor) {
        if (!(actor instanceof Client)) {
            throw new IllegalArgumentException("M2M v3 requires a client actor");
        }
        authSession.set(actor, tenant);
        return this;
    }

    @Override
    public M2Mv3UserJourney withConsumer(Tenant tenant, InteropActor actor) {
        if (!(actor instanceof Client)) {
            throw new IllegalArgumentException("M2M v3 requires a client actor");
        }
        authSession.set(actor, tenant);
        return this;
    }

    @Override
    public M2Mv3UserJourney withProducer(Tenant tenant, InteropRole role) {
        if (!(role instanceof M2MRole)) {
            throw new IllegalArgumentException("M2M v3 requires an M2M role");
        }
        authSession.set(role, tenant);
        return this;
    }

    @Override
    public M2Mv3UserJourney withConsumer(Tenant tenant, InteropRole role) {
        if (!(role instanceof M2MRole)) {
            throw new IllegalArgumentException("M2M v3 requires an M2M role");
        }
        authSession.set(role, tenant);
        return this;
    }
}
