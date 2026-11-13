package it.pagopa.interop.common.kernel.context;

import it.pagopa.interop.common.kernel.domain.InteropActor;
import it.pagopa.interop.common.kernel.domain.InteropRole;
import it.pagopa.interop.common.kernel.domain.Tenant;

public interface AuthSession {
    void set(InteropActor actor, Tenant tenant);

    void set(InteropRole role, Tenant tenant);

    InteropActor getActor();

    InteropRole getRole();

    Tenant getTenant();
}
