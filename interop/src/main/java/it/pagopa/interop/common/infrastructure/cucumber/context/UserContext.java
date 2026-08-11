package it.pagopa.interop.common.infrastructure.cucumber.context;

import it.pagopa.interop.common.infrastructure.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;

public class UserContext implements CurrentUserSession {
    private User currentUser;
    private Tenant currentTenant;

    @Override
    public void set(User user, Tenant tenant) {
        this.currentUser = user;
        this.currentTenant = tenant;
    }

    @Override
    public User getUser() {
        if (currentUser == null) throw new IllegalStateException("Current user is not set");
        return currentUser;
    }

    @Override
    public Tenant getTenant() {
        if (currentTenant == null) throw new IllegalStateException("Current tenant is not set");
        return currentTenant;
    }

    public boolean isLoggedIn(User user, Tenant tenant) {
        return this.currentUser != null
                && this.currentTenant != null
                && this.currentUser == user
                && this.currentTenant == tenant;
    }

    public void reset() {
        this.currentUser = null;
        this.currentTenant = null;
    }
}