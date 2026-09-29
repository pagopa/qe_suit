package it.pagopa.interop.m2m.infrastructure.context;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;

import java.util.concurrent.atomic.AtomicReference;

public class InMemoryCurrentM2MSession implements CurrentM2MSession {
    private final ThreadLocal<Tenant> currentTenant = new ThreadLocal<>();
    private final ThreadLocal<M2MRole> currentRole = new ThreadLocal<>();
    private final AtomicReference<Tenant> defaultTenant = new AtomicReference<>();
    private final AtomicReference<M2MRole> defaultRole = new AtomicReference<>();

    @Override
    public void set(Tenant tenant, M2MRole role) {
        defaultTenant.compareAndSet(null, tenant);
        defaultRole.compareAndSet(null, role);
        currentTenant.set(tenant);
        currentRole.set(role);
    }

    @Override
    public Tenant getTenant() {
        Tenant tenant = currentTenant.get();
        tenant = tenant != null ? tenant : defaultTenant.get();
        if (tenant == null) {
            throw new IllegalStateException("Current M2M tenant is not set");
        }
        return tenant;
    }

    @Override
    public M2MRole getRole() {
        M2MRole role = currentRole.get();
        role = role != null ? role : defaultRole.get();
        if (role == null) {
            throw new IllegalStateException("Current M2M role is not set");
        }
        return role;
    }
}
