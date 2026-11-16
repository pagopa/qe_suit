package it.pagopa.interop.m2m.v3.infrastructure.auth;

import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.InteropActor;
import it.pagopa.interop.common.kernel.domain.InteropRole;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.springframework.beans.factory.ObjectProvider;

public class InMemoryM2Mv3Session implements AuthSession {
    private final ObjectProvider<M2Mv3ApiClientProvider> clientProvider;
    private final ThreadLocal<InteropActor> actor = new ThreadLocal<>();
    private final ThreadLocal<InteropRole> role = new ThreadLocal<>();
    private final ThreadLocal<Tenant> tenant = new ThreadLocal<>();

    public InMemoryM2Mv3Session(ObjectProvider<M2Mv3ApiClientProvider> clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public void set(InteropActor actor, Tenant tenant) {
        if (!(actor instanceof Client)) {
            throw new IllegalArgumentException("M2M v3 session requires a client");
        }
        this.actor.set(actor);
        this.tenant.set(tenant);
    }

    @Override
    public void set(InteropRole role, Tenant tenant) {
        if (!(role instanceof M2MRole m2mRole)) {
            throw new IllegalArgumentException("M2M v3 session requires an M2M role");
        }
        Client client = clientProvider.getObject().getOrCreate(tenant, m2mRole);
        this.actor.set(client);
        this.role.set(role);
        this.tenant.set(tenant);
    }

    @Override
    public InteropActor getActor() {
        InteropActor value = actor.get();
        if (value == null) throw new IllegalStateException("Current actor is not set");
        return value;
    }

    @Override
    public InteropRole getRole() {
        InteropRole value = role.get();
        if (value == null) throw new IllegalStateException("Current role is not set");
        return value;
    }

    @Override
    public Tenant getTenant() {
        Tenant value = tenant.get();
        if (value == null) throw new IllegalStateException("Current tenant is not set");
        return value;
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.M2M_V3;
    }
}
