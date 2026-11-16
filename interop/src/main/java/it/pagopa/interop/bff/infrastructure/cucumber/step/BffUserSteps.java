package it.pagopa.interop.bff.infrastructure.cucumber.step;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.springframework.beans.factory.annotation.Qualifier;

public class BffUserSteps {
    private final AuthSession userSession;

    public BffUserSteps(@Qualifier("userAuthSession") AuthSession userSession) {
        this.userSession = userSession;
    }

    @Given("un {userRole} appartenente all'erogatore {tenant}")
    @Given("un {userRole} appartenente al fruitore {tenant}")
    @Given("un {userRole} appartenente al delegato {tenant}")
    public void setUser(UserRole role, Tenant tenant) {
        userSession.set(role, tenant);
    }

    @Given("un utente autenticato con ruolo {userRole} del tenant {tenant}")
    public void setAuth(UserRole userRole, Tenant tenant) {
        User user = User.getTenantUser(tenant, userRole);
        userSession.set(user, tenant);
    }
}
