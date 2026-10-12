package it.pagopa.interop.common.infrastructure.cucumber.step;

import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSteps {

    private final CurrentUserSession currentUserSession;

    @Given("un {userRole} appartenente all'erogatore {tenant}")
    @Given("un {userRole} appartenente al fruitore {tenant}")
    public void setUser(UserRole userRole, Tenant tenant) {
        currentUserSession.set(User.getTenantUser(tenant, userRole), tenant);
    }

}
