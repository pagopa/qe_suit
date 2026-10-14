package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.And;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceTemplateInterfaceSteps {
    private final EServiceTemplateUseCase templateUseCase;

    @And("l'interfaccia {templateVersion} è rimasta invariata")
    public void assertTemplateInterfaceUnchanged(EServiceTemplateVersion version) {
        templateUseCase.shouldHaveUnchangedInterface(version);
    }
}
