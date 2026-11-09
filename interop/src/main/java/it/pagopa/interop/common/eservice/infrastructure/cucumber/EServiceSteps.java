package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.journey.application.InteropJourney;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final InteropJourney interopJourney;
    private final EServiceUseCase eServiceUseCase;

    @Given("la versione {int} dell'{currentEService} (è )in fase di archiviazione con un periodo di preavviso di {gracePeriodDays} giorni")
    public void prepareArchivingDescriptor(int versionNumber, EService eService, GracePeriodDays gracePeriodDays) {
        descriptorUseCase.archiveDescriptor(
                eService,
                eService.findDescriptor(versionNumber).orElseThrow(() -> new IllegalStateException("Descriptor version not found")),
                gracePeriodDays
        );

        interopJourney.waitUntilEService(refreshed -> {
                    EServiceDescriptor descriptor = refreshed.findDescriptor(versionNumber).orElseThrow(() -> new IllegalStateException("Descriptor version not found"));
                    return descriptor.getState() == EServiceDescriptorState.ARCHIVING
                            && refreshed.getLatestDescriptor().getState() == EServiceDescriptorState.PUBLISHED;
                }
        );
    }

    @When("l'erogatore tenta di archiviare l'{currentEService} con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void archiveEService(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.archiveEService(eService, gracePeriodDays);
    }

    @Then("il sistema rifiuta l'archiviazione dell'{currentEService} da parte dell'erogatore con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void rejectEServiceArchiving(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.shouldFailToArchiveEService(
                eService, gracePeriodDays, EServiceArchivingFailureReason.ARCHIVING_BEFORE_DESCRIPTOR
        );
    }

    @Then("il sistema rifiuta la richiesta di archiviazione dell'{currentEService} da parte del delegato con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void rejectEServiceArchivingRequest(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.shouldFailToRequestEServiceArchiving(
                eService, gracePeriodDays, EServiceArchivingFailureReason.ARCHIVING_BEFORE_DESCRIPTOR
        );
    }
}
