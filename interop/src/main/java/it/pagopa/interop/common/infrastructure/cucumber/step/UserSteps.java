package it.pagopa.interop.common.infrastructure.cucumber.step;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSteps {

    private final AuthSession authSession;

    @Given("un {userRole} appartenente all'erogatore {tenant}")
    @Given("un {userRole} appartenente al fruitore {tenant}")
    @Given("un {userRole} appartenente al delegato {tenant}")
    public void setUser(UserRole userRole, Tenant tenant) {
        authSession.set(User.getTenantUser(tenant, userRole), tenant);
    }

    @Given("un {userM2MRole} appartenente all'erogatore {tenant}")
    @Given("un {userM2MRole} appartenente al fruitore {tenant}")
    public void setUser(M2MRole m2MRole, Tenant tenant) {
        authSession.set(m2MRole, tenant);
    }
}
