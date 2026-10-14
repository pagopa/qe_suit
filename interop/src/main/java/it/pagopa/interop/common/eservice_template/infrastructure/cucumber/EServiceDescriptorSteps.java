package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

@RequiredArgsConstructor
public class EServiceDescriptorSteps {
    private final EServiceDescriptorUseCase descriptorUseCase;

    @And("l'erogatore recupera l'interfaccia del descriptor {eServiceDescriptor} per l'{currentEService}")
    public void retrieveDescriptorInterface(EServiceDescriptor descriptor, EService eService) {
        descriptorUseCase.downloadInterface(eService, descriptor);
    }

    @Then("il descriptor {eServiceDescriptor} per l'{currentEService} è la versione 1 in stato bozza")
    public void assertFirstDraftDescriptor(EServiceDescriptor descriptor, EService eService) {
        EServiceDescriptor refreshed = descriptorUseCase.getDescriptor(eService, descriptor);
        Assertions.assertThat(refreshed.getVersion()).isEqualTo("1");
        Assertions.assertThat(refreshed.getState()).isEqualTo(EServiceDescriptorState.DRAFT);
    }

    @Then("non è disponibile un'interfaccia valida per il descriptor {eServiceDescriptor} per l'{currentEService}")
    public void assertNoAcceptedInterface(EServiceDescriptor descriptor, EService eService) {
        EServiceDescriptor refreshed = descriptorUseCase.getDescriptor(eService, descriptor);
        Assertions.assertThat(refreshed.getInterfaceDocument()).isNull();
    }

}
