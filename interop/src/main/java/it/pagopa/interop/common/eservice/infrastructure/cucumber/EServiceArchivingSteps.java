package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.application.EServiceArchivingUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceArchivingSteps {
    private final EServiceArchivingUseCase archiving;
    private final CurrentUserSession session;

    @Given("{eServiceByAlias} del/di {tenant} interamente in archiviazione")
    public void archiveEntireEService(EService eService, Tenant producer) {
        session.set(User.getTenantAdmin(producer), producer);
        archiving.archiveEntireEService(eService);
    }
}
