package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;

public class InMemoryCurrentUserSession implements CurrentUserSession {
    private final ThreadLocal<User> currentUser = new ThreadLocal<>();
    private final ThreadLocal<Tenant> currentTenant = new ThreadLocal<>();

    @Override
    public void set(User user, Tenant tenant) {
        this.currentUser.set(user);
        this.currentTenant.set(tenant);
    }

    @Override
    public User getUser() {
        User user = currentUser.get();
        if (user == null) throw new IllegalStateException("Current user is not set");
        return user;
    }

    @Override
    public Tenant getTenant() {
        Tenant tenant = currentTenant.get();
        if (tenant == null) throw new IllegalStateException("Current tenant is not set");
        return tenant;
    }
}
