package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;

public class InMemoryAuthSession implements AuthSession {
    private final ThreadLocal<InteropActor> currentActor = new ThreadLocal<>();
    private final ThreadLocal<Tenant> currentTenant = new ThreadLocal<>();
    private final ThreadLocal<InteropRole> currentRole = new ThreadLocal<>();

    @Override
    public void set(InteropActor actor, Tenant tenant) {
        this.currentActor.set(actor);
        this.currentTenant.set(tenant);
    }

    @Override
    public void set(InteropRole role, Tenant tenant) {
        this.currentRole.set(role);
        this.currentTenant.set(tenant);
        if(role instanceof UserRole userRole)
            currentActor.set(User.getTenantUser(tenant, userRole));
    }

    @Override
    public InteropActor getActor() {
        InteropActor actor = currentActor.get();
        if (actor == null) throw new IllegalStateException("Current user is not set");
        return actor;
    }

    @Override
    public InteropRole getRole() {
        InteropRole role = currentRole.get();
        if (role == null) throw new IllegalStateException("Current role is not set");
        return role;
    }

    @Override
    public Tenant getTenant() {
        Tenant tenant = currentTenant.get();
        if (tenant == null) throw new IllegalStateException("Current tenant is not set");
        return tenant;
    }
}
