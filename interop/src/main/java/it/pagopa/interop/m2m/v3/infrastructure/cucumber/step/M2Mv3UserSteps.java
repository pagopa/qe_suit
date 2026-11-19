package it.pagopa.interop.m2m.v3.infrastructure.cucumber.step;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.springframework.beans.factory.annotation.Qualifier;

public class M2Mv3UserSteps {
    private final AuthSession m2mSession;

    public M2Mv3UserSteps(@Qualifier("m2mV3AuthSession") AuthSession m2mSession) {
        this.m2mSession = m2mSession;
    }

    @Given("un {userM2MRole} appartenente all'erogatore {tenant}")
    @Given("un {userM2MRole} appartenente al fruitore {tenant}")
    public void setMachine(M2MRole role, Tenant tenant) {
        m2mSession.set(role, tenant);
    }
}
