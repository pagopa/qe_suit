package it.pagopa.interop.m2m.v3.infrastructure.auth;

import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.springframework.beans.factory.ObjectProvider;

public class CucumberM2Mv3Session implements AuthSession {
    private final ObjectProvider<M2Mv3ApiClientProvider> clientProvider;
    private InteropActor currentActor;
    private InteropRole currentRole;
    private Tenant currentTenant;

    public CucumberM2Mv3Session(ObjectProvider<M2Mv3ApiClientProvider> clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public void set(InteropActor actor, Tenant tenant) {
        if (!(actor instanceof Client)) {
            throw new IllegalArgumentException("M2M v3 session requires a client");
        }
        this.currentActor = actor;
        this.currentTenant = tenant;
    }

    @Override
    public void set(InteropRole role, Tenant tenant) {
        if (!(role instanceof M2MRole m2mRole)) {
            throw new IllegalArgumentException("M2M v3 session requires an M2M role");
        }
        this.currentActor = clientProvider.getObject().getOrCreate(tenant, m2mRole);
        this.currentRole = role;
        this.currentTenant = tenant;
    }

    @Override
    public InteropActor getActor() {
        if (currentActor == null) throw new IllegalStateException("Current actor is not set");
        return currentActor;
    }

    @Override
    public InteropRole getRole() {
        if (currentRole == null) throw new IllegalStateException("Current role is not set");
        return currentRole;
    }

    @Override
    public Tenant getTenant() {
        if (currentTenant == null) throw new IllegalStateException("Current tenant is not set");
        return currentTenant;
    }

    public void reset() {
        this.currentActor = null;
        this.currentRole = null;
        this.currentTenant = null;
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.M2M_V3;
    }
}