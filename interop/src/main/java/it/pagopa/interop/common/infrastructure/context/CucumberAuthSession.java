package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;

public class CucumberAuthSession implements AuthSession {
    private InteropActor currentActor;
    private InteropRole currentRole;
    private Tenant currentTenant;

    @Override
    public void set(InteropActor actor, Tenant tenant) {
        this.currentActor = actor;
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
        this.currentTenant = null;
    }
}