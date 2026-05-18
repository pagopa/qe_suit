package it.pagopa.interop.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.UUID;

import static it.pagopa.interop.domain.enums.Tenant.*;
import static it.pagopa.interop.domain.enums.UserRole.ADMIN;

@Getter
@RequiredArgsConstructor
public enum User {
    S_MATTIA(ADMIN, UUID.fromString("5d717b97-5308-49a5-8682-187663278f24"), "s.mattia", "test", new Tenant[]{COMUNE_DI_MILANO, COMUNE_DI_POZZALLO, COMUNE_DI_COMUN_NUOVO, PAGO_PA, KYMA});

    private final UserRole role;
    private final UUID userId;
    private final String username;
    private final String password;
    private final Tenant[] tenants;

    public static User getTenantAdmin(Tenant tenant) {
        return getTenantUser(tenant, ADMIN);
    }

    public static User getTenantUser(Tenant tenant, UserRole role) {
        return Arrays.stream(User.values()).filter(user -> user.getRole() == role)
                .filter(user -> Arrays.asList(user.getTenants()).contains(tenant))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No user found in tenant %s for role %s", tenant.name(), role.name())));
    }
}
