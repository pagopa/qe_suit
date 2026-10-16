package it.pagopa.interop.m2m.kernel.context;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;

public interface CurrentM2MSession {
    void set(Tenant tenant, M2MRole role);

    Tenant getTenant();

    M2MRole getRole();
}
