package it.pagopa.interop.common.infrastructure.cucumber.step;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSteps {

    // TODO La gestione dell'autenticazione per canale deve essere incapsulata e automatizzata nei livelli sottostanti.

    private final CurrentUserSession currentUserSession;

    private final CurrentM2MSession currentM2MSession;

    @Given("un {userRole} appartenente all'erogatore {tenant}")
    @Given("un {userRole} appartenente al fruitore {tenant}")
    public void setUser(UserRole userRole, Tenant tenant) {
        currentUserSession.set(User.getTenantUser(tenant, userRole), tenant);
    }

    @Given("un {userM2MRole} appartenente all'erogatore {tenant}")
    @Given("un {userM2MRole} appartenente al fruitore {tenant}")
    public void setUser(M2MRole userRole, Tenant tenant) {
        currentM2MSession.set(tenant, userRole);
    }
}
