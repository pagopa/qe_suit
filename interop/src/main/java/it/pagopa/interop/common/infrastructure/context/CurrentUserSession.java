package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;

public interface CurrentUserSession {
    void set(User user, Tenant tenant);

    User getUser();

    Tenant getTenant();

    default boolean isLoggedIn(User user, Tenant tenant) {
        return this.getUser() != null
                && this.getTenant() != null
                && this.getUser() == user
                && this.getTenant() == tenant;
    }
}
