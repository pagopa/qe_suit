package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;

public interface CurrentUserSession {
    void set(User user, Tenant tenant);

    User getUser();

    Tenant getTenant();
}
