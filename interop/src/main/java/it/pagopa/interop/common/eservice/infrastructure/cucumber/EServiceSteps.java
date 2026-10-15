package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.journey.application.InteropJourney;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final InteropJourney interopJourney;

    @Given("la versione {int} dell'{currentEService} è in fase di archiviazione con un periodo di preavviso di {gracePeriodDays} giorni")
    public void prepareArchivingDescriptor(int versionNumber, EService eService, GracePeriodDays gracePeriodDays) {
        descriptorUseCase.archiveDescriptor(eService, eService.getDescriptor(versionNumber), gracePeriodDays);
        interopJourney.waitUntilEService(refreshed ->
                refreshed.getDescriptor(versionNumber).getState() == EServiceDescriptorState.ARCHIVING
                        && refreshed.getLatestDescriptor().getState() == EServiceDescriptorState.PUBLISHED
        );
    }
}
