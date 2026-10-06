package it.pagopa.interop.common.kernel.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.UUID;

import static it.pagopa.interop.common.kernel.domain.Tenant.*;
import static it.pagopa.interop.common.kernel.domain.UserRole.*;

@Getter
@RequiredArgsConstructor
public enum User {
    S_MATTIA(ADMIN, UUID.fromString("5d717b97-5308-49a5-8682-187663278f24"), "s.mattia", "test", new Tenant[]{COMUNE_DI_MILANO, COMUNE_DI_POZZALLO, COMUNE_DI_COMUN_NUOVO, PAGO_PA, KYMA}),
    E_ZANARDI(API, UUID.fromString("e3f51f86-3814-4f7f-96f6-552e086bae8d"), "e.zanardi", "test", new Tenant[]{COMUNE_DI_MILANO, COMUNE_DI_POZZALLO, COMUNE_DI_COMUN_NUOVO}),
    TIMOTEO_PINTO(API_SECURITY, UUID.fromString("e0477bcc-3baf-4755-aa31-375c051acb44"), "timoteo.pinto", "test", new Tenant[]{COMUNE_DI_MILANO, COMUNE_DI_POZZALLO, COMUNE_DI_COMUN_NUOVO}),
    G_PEDRONI(SECURITY, UUID.fromString("1d3d77d0-c1bd-420a-b748-998e373f7c81"), "g.pedroni", "test", new Tenant[]{COMUNE_DI_MILANO, COMUNE_DI_POZZALLO, COMUNE_DI_COMUN_NUOVO}),
    G_PEDRONI_SUPPORT(SUPPORT, UUID.fromString("1d3d77d0-c1bd-420a-b748-998e373f7c81"), "g.pedroni", "test", new Tenant[]{COMUNE_DI_MILANO, COMUNE_DI_POZZALLO, COMUNE_DI_COMUN_NUOVO}),
    CESARE(REVIEWER, UUID.fromString("f8cda410-622d-47a6-9365-506b361258cf"), "cesare", "password123", new Tenant[]{AGID, COMUNE_DI_POZZALLO, COMUNE_DI_COMUN_NUOVO, PAGO_PA, KYMA}),
    B_BARNES(VIEWER, UUID.fromString("46af2a8a-6c77-4b95-83a1-9748b4c64cc4"), "b.barnes", "test", new Tenant[]{AGID, COMUNE_DI_POZZALLO, COMUNE_DI_COMUN_NUOVO, PAGO_PA, KYMA}),
    IGLESIAS(ADMIN, UUID.fromString("c27e3508-3d26-4b6b-9c73-54cb38e6fe1b"), "iglesias", "test", new Tenant[]{SOGECAP}),
    A_CASTIGLIONE(API_SECURITY, UUID.fromString("17a84b7b-dce6-4b8f-a1ae-85926c55f02e"), "alessia.castiglione", "test", new Tenant[]{SOGECAP}),
    TIMOTEO_PINTO_SOGECAP(API, UUID.fromString("e0477bcc-3baf-4755-aa31-375c051acb44"), "timoteo.pinto", "test", new Tenant[]{SOGECAP}),
    D_ALIGHIERI(SECURITY, UUID.fromString("e490f02e-9429-4b38-bb11-ddb8a561fb62"), "DanteAlighieri", "test", new Tenant[]{SOGECAP}),
    D_ALIGHIERI_SUPPORT(SUPPORT, UUID.fromString("e490f02e-9429-4b38-bb11-ddb8a561fb62"), "DanteAlighieri", "test", new Tenant[]{SOGECAP}),
    C_MARINI(REVIEWER, UUID.fromString("c1c8ec42-82b2-4ea3-8088-f18846668d23"), "cmarini", "test", new Tenant[]{SOGECAP}),
    MICHELANGELO(VIEWER, UUID.fromString("29d47f36-85d2-42a2-aa49-f520e16d9e00"), "michelangelo", "password123", new Tenant[]{SOGECAP});

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
