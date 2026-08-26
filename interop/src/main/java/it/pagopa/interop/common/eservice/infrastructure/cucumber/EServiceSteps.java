package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.infrastructure.context.cucumber.UserContext;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceUseCase eServiceUseCase;
    private final UserContext userContext;

    @When("{tenant} crea una nuova versione del {currentEService}")
    public void createNewEserviceVersion(Tenant tenant, EService eService){
        userContext.set(User.getTenantAdmin(tenant), tenant);
        eServiceUseCase.addDescriptor(eService);
    }
}
