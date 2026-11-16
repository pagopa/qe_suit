package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;

public class InMemoryUserSession implements AuthSession {
    private final ThreadLocal<InteropActor> currentActor = new ThreadLocal<>();
    private final ThreadLocal<Tenant> currentTenant = new ThreadLocal<>();
    private final ThreadLocal<InteropRole> currentRole = new ThreadLocal<>();

    @Override
    public void set(InteropActor actor, Tenant tenant) {
        if (!(actor instanceof User user)) {
            throw new IllegalArgumentException("User session requires a user actor");
        }
        this.currentActor.set(actor);
        this.currentTenant.set(tenant);
        this.currentRole.set(user.getRole());
    }

    @Override
    public void set(InteropRole role, Tenant tenant) {
        if (!(role instanceof UserRole userRole)) {
            throw new IllegalArgumentException("User session requires a user role");
        }
        User user = User.getTenantUser(tenant, userRole);
        this.currentActor.set(user);
        this.currentRole.set(role);
        this.currentTenant.set(tenant);
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

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF || delimiter == Channel.WEB_BROWSER;
    }
}
