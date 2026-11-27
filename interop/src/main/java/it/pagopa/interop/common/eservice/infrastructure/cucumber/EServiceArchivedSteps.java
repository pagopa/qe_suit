package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceArchivedSteps {
    // 19/11/2026 In ambiente QA al momento possiamo avere e-service interamente archiviati solo
    // pre-generati in dase di data preparation, e questo è uno di quelli
    private static final UUID ARCHIVED_ESERVICE_ID = UUID.fromString("45d822b9-066c-4a77-b4a1-331135496907");

    private final EServiceUseCase eServiceUseCase;
    private final EntityStore entityStore;
    private final AuthSession session;

    @Given("l'e-service {string} del/di {tenant} interamente archiviato")
    public void eServiceEntirelyArchived(String eServiceAlias, Tenant producer) {
        // Usato negli scenari con id ERROR_204
        if (eServiceAlias == null || eServiceAlias.isBlank()) {
            throw new IllegalArgumentException("E-service alias must not be blank");
        }
        session.set(producer);
        EService archivedEService = eServiceUseCase.getEService(EServiceRef.of(ARCHIVED_ESERVICE_ID));
        entityStore.upsert(eServiceAlias, archivedEService);
    }
}
