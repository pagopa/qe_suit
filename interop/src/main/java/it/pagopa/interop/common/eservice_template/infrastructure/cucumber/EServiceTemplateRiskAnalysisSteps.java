package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceTemplateSOAPInterfaceFailureReason;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInstanceCreationFailureReason;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceTemplateRiskAnalysisSteps {
    private final EServiceTemplateInstanceUseCase instanceUseCase;
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final InteropJourney interopJourney;
    private final EntityStore entities;

    @Given("un'analisi del rischio completa associata al template")
    public void completeRiskAnalysisAssociatedToTemplate() {
        // TODO
    }
}
