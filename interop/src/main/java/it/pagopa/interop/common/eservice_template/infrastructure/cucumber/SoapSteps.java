package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.application.SoapUseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SoapSteps {
    private final SoapUseCase useCase;

    @Then("ogni porta del WSDL del {eServiceDescriptor} contiene l'indirizzo configurato per quella porta:")
    public void assertConfiguredWsdlAddresses(
            EServiceDescriptor descriptor,
            List<SoapAddressConfiguration> expected
    ) {
        useCase.shouldContainAddresses(descriptor, expected);
    }

    @And("il WSDL del {eServiceDescriptor} è identico byte per byte all'interfaccia {templateVersion} salvo gli indirizzi configurati")
    public void assertOnlyAddressesChanged(
            EServiceDescriptor descriptor,
            EServiceTemplateVersion templateVersion
    ) {
        useCase.shouldPreserveBytes(templateVersion, descriptor);
    }

    @And("il WSDL del {eServiceDescriptor} è XML ben formato")
    public void assertWellFormed(EServiceDescriptor descriptor) {
        useCase.shouldBeWellFormed(descriptor);
    }
}
