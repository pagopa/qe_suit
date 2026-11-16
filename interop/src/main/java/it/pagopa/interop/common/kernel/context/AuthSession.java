package it.pagopa.interop.common.kernel.context;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.InteropActor;
import it.pagopa.interop.common.kernel.domain.InteropRole;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.springframework.plugin.core.Plugin;

public interface AuthSession extends Plugin<Channel> {
    void set(InteropActor actor, Tenant tenant);

    void set(Tenant tenant);

    void set(InteropRole role, Tenant tenant);

    InteropActor getActor();

    InteropRole getRole();

    Tenant getTenant();
}
