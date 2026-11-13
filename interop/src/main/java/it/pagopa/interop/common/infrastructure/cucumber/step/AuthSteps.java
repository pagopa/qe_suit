package it.pagopa.interop.common.infrastructure.cucumber.step;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthSteps {
    private final AuthSession authSession;

    @Given("un utente autenticato del/di {tenant} con ruolo {userRole}")
    public void setAuth(Tenant tenant, UserRole userRole) {
        authSession.set(User.getTenantUser(tenant, userRole), tenant);
    }

    @Given("un utente autenticato con ruolo {userRole} di un tenant di tipo {tenantKind}")
    public void setAuth(UserRole userRole, TenantKind tenantKind) {
        Tenant tenant = Tenant.fromTenantType(tenantKind);
        setAuth(userRole, tenant);
    }

    @Given("un utente autenticato con ruolo {userRole} del tenant {tenant}")
    public void setAuth(UserRole userRole, Tenant tenant) {
        User user = User.getTenantUser(tenant, userRole);
        authSession.set(user, tenant);
    }
}
