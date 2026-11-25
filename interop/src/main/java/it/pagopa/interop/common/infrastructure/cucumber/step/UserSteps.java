package it.pagopa.interop.common.infrastructure.cucumber.step;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSteps {

    private final AuthSession authSession;

    @Given("un admin di/del {tenant}")
    public void setAdmin(Tenant tenant) {
        authSession.set(tenant);
    }
}
