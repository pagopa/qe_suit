package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.application.EServiceArchivingUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceArchivedSteps {
    private final EServiceArchivingUseCase archiving;
    private final AuthSession session;

    @Given("l'e-service {string} del/di {tenant} interamente archiviato")
    public void eServiceEntirelyArchived(String eServiceAlias, Tenant producer) {
        // TODO
    }
}
