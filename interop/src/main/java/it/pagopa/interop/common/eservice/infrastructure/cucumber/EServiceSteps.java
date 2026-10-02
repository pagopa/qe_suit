package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceUseCase eServiceUseCase;
    private final EntityStore entityStore;
    private final CurrentUserSession currentUserSession;

    @When("legge il valore {int} per la soglia di chiamate API personalizzata per l'EService da catalogo")
    public void readValueOnApiCallsCustomThresholdForEServiceFromCatalog(int thresholdValue) {
        EService eServiceFromCatalog = eServiceUseCase.getEServiceFromCatalog(
                entityStore.getLastOrThrow(EService.class)
        );
        // TODO dopo il rilascio in QA eServiceFromCatalog dovrebbe poter ricevere il thresholdValue personalizzato
    }
}
