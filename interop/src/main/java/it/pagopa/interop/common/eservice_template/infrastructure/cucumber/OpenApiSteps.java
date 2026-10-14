package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.application.OpenApiUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpenApiSteps {
    private final OpenApiUseCase useCase;

    @Then("la specifica OpenAPI del descriptor {eServiceDescriptor} contiene i seguenti dati:")
    public void assertConfiguredValues(
            EServiceDescriptor descriptor,
            RestInterfaceConfiguration expected
    ) {
        useCase.shouldContainConfiguration(descriptor, expected);
    }

    @And("la specifica OpenAPI del descriptor {eServiceDescriptor} conserva schemi e riferimenti dell'interfaccia {templateVersion}")
    public void assertStructurePreserved(
            EServiceDescriptor descriptor,
            EServiceTemplateVersion templateVersion
    ) {
        useCase.shouldPreserveStructure(templateVersion, descriptor);
    }

    @And("la specifica OpenAPI del descriptor {eServiceDescriptor} è valida")
    public void assertValid(EServiceDescriptor descriptor) {
        useCase.shouldBeValid(descriptor);
    }
}
