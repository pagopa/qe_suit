package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;

public class CucumberUserSession implements AuthSession {
    private InteropActor currentActor;
    private InteropRole currentRole;
    private Tenant currentTenant;

    @Override
    public void set(InteropActor actor, Tenant tenant) {
        if (!(actor instanceof User user)) {
            throw new IllegalArgumentException("User session requires a user actor");
        }
        this.currentActor = actor;
        this.currentTenant = tenant;
        this.currentRole = user.getRole();
    }

    @Override
    public void set(Tenant tenant) {
        this.currentActor = User.getTenantAdmin(tenant);
        this.currentTenant = tenant;
        this.currentRole = UserRole.ADMIN;
    }

    @Override
    public void set(InteropRole role, Tenant tenant) {
        if (!(role instanceof UserRole userRole)) {
            throw new IllegalArgumentException("User session requires a user role");
        }
        User user = User.getTenantUser(tenant, userRole);
        this.currentActor = user;
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
        return delimiter == Channel.BFF || delimiter == Channel.WEB_BROWSER;
    }
}