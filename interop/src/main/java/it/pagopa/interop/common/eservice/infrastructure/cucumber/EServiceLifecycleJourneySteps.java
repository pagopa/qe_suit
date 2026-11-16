package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.SelectedEServiceDescriptor;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceLifecycleJourneySteps {

    private final InteropJourney interopJourney;

    @Given("{eServiceDescriptorByVersionAndAlias} del/di {tenant} {eServiceDescriptorState}")
    @Given("{eServiceDescriptorByVersionAndAlias} predisposta dal delegato {tenant} {eServiceDescriptorState}")
    public void bringEServiceDescriptorToState(SelectedEServiceDescriptor selectedEServiceDescriptor, Tenant producer, EServiceDescriptorState targetState) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .bringEServiceDescriptorToState(
                        selectedEServiceDescriptor.getEService(),
                        selectedEServiceDescriptor.getVersion(),
                        targetState
                );
    }
}
