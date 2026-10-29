package it.pagopa.interop.common.infrastructure.contract;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;

import java.util.Objects;

/**
 * Utenza di un test di autorizzazione: BFF ({@code user}) oppure M2M ({@code m2mRole}),
 * sempre riferita a un tenant. L'etichetta non contiene credenziali.
 */
public record InteropContractIdentity(String label, Tenant tenant, User user, M2MRole m2mRole) {

    public InteropContractIdentity {
        Objects.requireNonNull(tenant, "tenant must not be null");
        if ((user == null) == (m2mRole == null)) {
            throw new IllegalArgumentException("exactly one of user and m2mRole must be set");
        }
    }

    public static InteropContractIdentity bff(Tenant tenant, User user) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(user, "user must not be null");
        return new InteropContractIdentity(tenant.getName() + " / " + user.getRole(), tenant, user, null);
    }

    public static InteropContractIdentity bff(Tenant tenant, UserRole role) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(role, "role must not be null");
        return bff(tenant, User.getTenantUser(tenant, role));
    }

    public static InteropContractIdentity m2m(Tenant tenant, M2MRole role) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(role, "role must not be null");
        return new InteropContractIdentity(tenant.getName() + " / " + role, tenant, null, role);
    }
}
