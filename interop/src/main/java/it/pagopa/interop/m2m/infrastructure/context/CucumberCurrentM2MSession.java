package it.pagopa.interop.m2m.infrastructure.context;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;

public class CucumberCurrentM2MSession implements CurrentM2MSession {

    private Tenant tenant;
    private M2MRole role;

    @Override
    public void set(Tenant tenant, M2MRole role) {
        this.tenant = tenant;
        this.role = role;
    }

    @Override
    public Tenant getTenant() {
        return tenant;
    }

    @Override
    public M2MRole getRole() {
        return role;
    }
}